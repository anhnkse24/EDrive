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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

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
        // =======================
        // Tạo Đại Lý
        // =======================
        Dealer dealer1 = new Dealer();
        dealer1.setDealerName("Edriver Center");
        dealer1.setAddress("123 Nguyen Van Troi, Phu Nhuan, TP. HCM");
        dealer1.setContactPerson("Nguyen Van A");
        dealer1.setPhone("0909123456");
        dealerRepository.save(dealer1);

        Dealer dealer2 = new Dealer();
        dealer2.setDealerName("Edrive Hanoi Showroom");
        dealer2.setAddress("25 Tran Duy Hung, Cau Giay, Ha Noi");
        dealer2.setContactPerson("Tran Thi B");
        dealer2.setPhone("0912345678");
        dealerRepository.save(dealer2);

        Dealer dealer3 = new Dealer();
        dealer3.setDealerName("Edrive Danang Center");
        dealer3.setAddress("78 Nguyen Van Linh, Hai Chau, Da Nang");
        dealer3.setContactPerson("Le Van C");
        dealer3.setPhone("0938765432");
        dealerRepository.save(dealer3);

        Dealer dealer4 = new Dealer();
        dealer4.setDealerName("Edrive Can Tho Auto Mall");
        dealer4.setAddress("45 Cach Mang Thang 8, Ninh Kieu, Can Tho");
        dealer4.setContactPerson("Pham Thi D");
        dealer4.setPhone("0978123456");
        dealerRepository.save(dealer4);

        Dealer dealer5 = new Dealer();
        dealer5.setDealerName("Edrive Hai Phong Premium Showroom");
        dealer5.setAddress("12 Le Hong Phong, Ngo Quyen, Hai Phong");
        dealer5.setContactPerson("Do Van E");
        dealer5.setPhone("0989988776");
        dealerRepository.save(dealer5);

        // =======================
        // Tạo User
        // =======================
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("Admin User");
        admin.setEmail("admin@edriver.com");
        admin.setPhone("0909000001");
        admin.setRole(UserRole.ADMIN);
        admin.setDealer(dealer1);
        userRepository.save(admin);

        User staff = new User();
        staff.setUsername("staff1");
        staff.setPassword(passwordEncoder.encode("staff123"));
        staff.setFullName("Staff One");
        staff.setEmail("staff1@edriver.com");
        staff.setPhone("0909000002");
        staff.setRole(UserRole.DEALER_STAFF);
        staff.setDealer(dealer1);
        userRepository.save(staff);

        // =======================
        // Tạo Vehicles (rút gọn)
        // =======================
        Vehicle v1 = new Vehicle();
        v1.setModelName("E-Car A");
        v1.setVersion("Standard");
        v1.setColor("Red");
        v1.setBatteryCapacityKwh(70);
        v1.setRangeKm(380);
        v1.setMaxSpeedKmh(145);
        v1.setChargingTimeHours(1.5f);
        v1.setSeatingCapacity(5);
        v1.setMotorPowerKw(140);
        v1.setWeightKg(1750);
        v1.setLengthMm(4450);
        v1.setWidthMm(1800);
        v1.setHeightMm(1580);
        v1.setPriceRetail(1150000000.0);
        v1.setStatus(VehicleStatus.DISCONTINUED);
        v1.setManufactureYear(2022);
        vehicleRepository.save(v1);

        Vehicle v2 = new Vehicle();
        v2.setModelName("E-Car A");
        v2.setVersion("Premium");
        v2.setColor("Blue");
        v2.setBatteryCapacityKwh(85);
        v2.setRangeKm(450);
        v2.setMaxSpeedKmh(160);
        v2.setChargingTimeHours(1.8f);
        v2.setSeatingCapacity(5);
        v2.setMotorPowerKw(160);
        v2.setWeightKg(1850);
        v2.setLengthMm(4500);
        v2.setWidthMm(1820);
        v2.setHeightMm(1600);
        v2.setPriceRetail(1350000000.0);
        v2.setStatus(VehicleStatus.DISCONTINUED);
        v2.setManufactureYear(2023);
        vehicleRepository.save(v2);

        // =======================
        // Customers
        // =======================
        Customer c1 = new Customer();
        c1.setFullName("Nguyễn Minh Hòa");
        c1.setDob(LocalDate.of(1998, 5, 12));
        c1.setGender("Nam");
        c1.setEmail("hoa.nguyen@example.com");
        c1.setPhone("0909123456");
        c1.setAddress("12 Lê Lợi, Q1, TP.HCM");
        c1.setIdCardNo("079123456789");
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
        // Helper: làm tròn giờ về mốc 00/30 phút
        // =======================
        Function<LocalDateTime, LocalDateTime> roundToSlot = dt -> {
            int m = dt.getMinute();
            if (m == 0 || m == 30) return dt.withSecond(0).withNano(0);
            if (m < 30) return dt.withMinute(30).withSecond(0).withNano(0);
            return dt.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        };

        LocalTime OPEN = LocalTime.of(8, 0);
        LocalTime CLOSE = LocalTime.of(17, 30);
        Duration SLOT = Duration.ofMinutes(30);

        // =======================
        // TestDrives
        // =======================
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        LocalDate threeDaysAgo = today.minusDays(3);

        // --- Lịch hẹn tương lai (ngày mai)
        TestDrive td1 = new TestDrive(c1, dealer1, v1, LocalDateTime.of(tomorrow, LocalTime.of(9, 0)), TestDriveStatus.PENDING);
        testDriveRepository.save(td1);
        TestDrive td2 = new TestDrive(c2, dealer1, v2, LocalDateTime.of(tomorrow, LocalTime.of(9, 30)), TestDriveStatus.PENDING);
        testDriveRepository.save(td2);
        TestDrive td3 = new TestDrive(c3, dealer1, v1, LocalDateTime.of(tomorrow, LocalTime.of(14, 30)), TestDriveStatus.PENDING);
        testDriveRepository.save(td3);

        // --- Lịch hẹn hôm nay (hiện tại + 3 tiếng)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime slot1 = roundToSlot.apply(now.plusHours(3));
        LocalDateTime slot2 = slot1.plusMinutes(30);
        if (slot2.toLocalTime().isAfter(CLOSE.minus(SLOT))) {
            slot2 = LocalDateTime.of(today, CLOSE.minus(SLOT));
        }

        TestDrive tdToday1 = new TestDrive(c2, dealer1, v1, slot1, TestDriveStatus.PENDING);
        testDriveRepository.save(tdToday1);
        TestDrive tdToday2 = new TestDrive(c3, dealer1, v2, slot2, TestDriveStatus.PENDING);
        testDriveRepository.save(tdToday2);

        // --- Lịch hẹn đã qua (hôm qua & 3 ngày trước)
        TestDrive tdPast1 = new TestDrive(c1, dealer1, v1, LocalDateTime.of(yesterday, LocalTime.of(10, 0)), TestDriveStatus.COMPLETED);
        testDriveRepository.save(tdPast1);
        TestDrive tdPast2 = new TestDrive(c2, dealer1, v2, LocalDateTime.of(yesterday, LocalTime.of(10, 30)), TestDriveStatus.COMPLETED);
        testDriveRepository.save(tdPast2);
        TestDrive tdPast3 = new TestDrive(c3, dealer1, v1, LocalDateTime.of(threeDaysAgo, LocalTime.of(15, 0)), TestDriveStatus.CANCELLED);
        testDriveRepository.save(tdPast3);
        TestDrive tdPast4 = new TestDrive(c1, dealer1, v2, LocalDateTime.of(threeDaysAgo, LocalTime.of(15, 30)), TestDriveStatus.COMPLETED);
        testDriveRepository.save(tdPast4);

        System.out.println("✅ Data initialization completed!");
    }
}
