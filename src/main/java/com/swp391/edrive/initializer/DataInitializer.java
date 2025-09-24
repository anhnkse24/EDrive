package com.swp391.edrive.initializer;

import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.enums.UserRole;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.UserRepository;
import com.swp391.edrive.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DealerRepository dealerRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public DataInitializer(DealerRepository dealerRepository, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.dealerRepository = dealerRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
// =======================
        // Tạo Dealer
        // =======================
        Dealer dealer1 = new Dealer();
        dealer1.setDealerName("Edriver Center");
        dealer1.setAddress("123 Nguyen Van Troi, HCMC");
        dealer1.setContactPerson("Nguyen Van A");
        dealer1.setPhone("0909123456");
        dealerRepository.save(dealer1);

        // =======================
        // Tạo User
        // =======================
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("{noop}admin123"); // dùng {noop} nếu chưa encode mật khẩu
        admin.setFullName("Admin User");
        admin.setEmail("admin@edriver.com");
        admin.setPhone("0909000001");
        admin.setRole(UserRole.ADMIN);
        admin.setDealer(dealer1);
        userRepository.save(admin);

        User staff = new User();
        staff.setUsername("staff1");
        staff.setPassword("staff123");
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

        System.out.println("Data initialization completed!");
    }
}

