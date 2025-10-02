package com.swp391.edrive.initializer;

import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.TestDriveStatus;
import com.swp391.edrive.enums.UserRole;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DealerRepository dealerRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final TestDriveRepository testDriveRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Dealer dealer1 = new Dealer();
        dealer1.setDealerName("Edriver Center");
        dealer1.setAddress("123 Nguyen Van Troi, HCMC");
        dealer1.setContactPerson("Nguyen Van A");
        dealer1.setPhone("0909123456");
        dealerRepository.save(dealer1);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123")); // mã hoá
        admin.setFullName("Admin User");
        admin.setEmail("admin@edriver.com");
        admin.setPhone("0909000001");
        admin.setRole(UserRole.ADMIN);
        admin.setDealer(dealer1);
        userRepository.save(admin);

        User staff = new User();
        staff.setUsername("staff1");
        staff.setPassword(passwordEncoder.encode("staff123")); // mã hoá
        staff.setFullName("Staff One");
        staff.setEmail("staff1@edriver.com");
        staff.setPhone("0909000002");
        staff.setRole(UserRole.DEALER_STAFF);
        staff.setDealer(dealer1);
        userRepository.save(staff);

        // =======================
        // Tạo Vehicles
        // =======================
        Vehicle v1 = new Vehicle();
        v1.setModelName("E-Car A");
        v1.setVersion("2025");
        v1.setColor("Red");
        v1.setBatteryCapacityKwh(75);
        v1.setRangeKm(400);
        v1.setMaxSpeedKmh(150);
        v1.setChargingTimeHours(1.5f);
        v1.setSeatingCapacity(5);
        v1.setMotorPowerKw(150);
        v1.setWeightKg(1800);
        v1.setLengthMm(4500);
        v1.setWidthMm(1800);
        v1.setHeightMm(1600);
        v1.setPriceRetail(1200000000.0);
        v1.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(v1);

        Vehicle v2 = new Vehicle();
        v2.setModelName("E-Car B");
        v2.setVersion("2025");
        v2.setColor("Blue");
        v2.setBatteryCapacityKwh(85);
        v2.setRangeKm(450);
        v2.setMaxSpeedKmh(160);
        v2.setChargingTimeHours(2f);
        v2.setSeatingCapacity(5);
        v2.setMotorPowerKw(160);
        v2.setWeightKg(1850);
        v2.setLengthMm(4600);
        v2.setWidthMm(1850);
        v2.setHeightMm(1620);
        v2.setPriceRetail(1350000000.0);
        v2.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(v2);

        // =======================
        // Customers
        // =======================
        Customer c1 = new Customer();
        c1.setFullName("Nguyễn Minh Hòa");
        c1.setDob(LocalDate.of(1998, 5, 12));
        c1.setGender("Nam");
        c1.setEmail("hoa.nguyen@example.com");
        c1.setPhone("0909123456"); // 10 số bắt đầu 0
        c1.setAddress("12 Lê Lợi, Q1, TP.HCM");
        c1.setIdCardNo("079123456789"); // 9–12 số
        customerRepository.save(c1);

        Customer c2 = new Customer();
        c2.setFullName("Trần Thu Hà");
        c2.setDob(LocalDate.of(1996, 11, 3));
        c2.setGender("Nữ");
        c2.setEmail("ha.tran@example.com");
        c2.setPhone("0911222333");
        c2.setAddress("45 Hai Bà Trưng, Q1, TP.HCM");
        c2.setIdCardNo("031234567");
        customerRepository.save(c2);

        Customer c3 = new Customer();
        c3.setFullName("Phạm Quang Khải");
        c3.setDob(LocalDate.of(1993, 2, 20));
        c3.setGender("Khác");
        c3.setEmail("khai.pham@example.com");
        c3.setPhone("0933666777");
        c3.setAddress("99 Phạm Văn Đồng, Thủ Đức, TP.HCM");
        c3.setIdCardNo("123456789012");
        customerRepository.save(c3);

        // =======================
        // TestDrives (seed để test available/book)
        // =======================
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        TestDrive td1 = new TestDrive();
        td1.setCustomer(c1);
        td1.setDealer(dealer1);
        td1.setVehicle(v1);
        td1.setScheduleDatetime(LocalDateTime.of(tomorrow, LocalTime.of(9, 0)));
        td1.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(td1);

        TestDrive td2 = new TestDrive();
        td2.setCustomer(c2);
        td2.setDealer(dealer1);
        td2.setVehicle(v2);
        td2.setScheduleDatetime(LocalDateTime.of(tomorrow, LocalTime.of(9, 30)));
        td2.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(td2);

        TestDrive td3 = new TestDrive();
        td3.setCustomer(c3);
        td3.setDealer(dealer1);
        td3.setVehicle(v1);
        td3.setScheduleDatetime(LocalDateTime.of(tomorrow, LocalTime.of(14, 30)));
        td3.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(td3);

        System.out.println("Data initialization completed! (dealers, users, vehicles, customers, test drives)");
    }
}

