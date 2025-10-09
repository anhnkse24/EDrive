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

import java.math.BigDecimal;
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
        // Tạo User (Hãng + mỗi đại lý)
        // =======================
        createUserIfAbsent("admin", "admin123", "Admin User",
                "admin@edriver.com", "0909000001", UserRole.ADMIN, null);
        createUserIfAbsent("evm1", "evm123", "EVM Staff 1",
                "evm1@edriver.com", "0909000003", UserRole.EVM_STAFF, null);

        createUserIfAbsent("manager_hcm", "manager123", "Dealer Manager HCM",
                "manager_hcm@edriver.com", "0909111222", UserRole.DEALER_MANAGER, dealer1);
        createUserIfAbsent("staff_hcm1", "staff123", "Dealer Staff HCM 1",
                "staff_hcm1@edriver.com", "0909555666", UserRole.DEALER_STAFF, dealer1);
        createUserIfAbsent("staff_hcm2", "staff123", "Dealer Staff HCM 2",
                "staff_hcm2@edriver.com", "0909555777", UserRole.DEALER_STAFF, dealer1);

        createUserIfAbsent("manager_hn", "manager123", "Dealer Manager HN",
                "manager_hn@edriver.com", "0909222333", UserRole.DEALER_MANAGER, dealer2);
        createUserIfAbsent("staff_hn1", "staff123", "Dealer Staff HN 1",
                "staff_hn1@edriver.com", "0909777888", UserRole.DEALER_STAFF, dealer2);
        createUserIfAbsent("staff_hn2", "staff123", "Dealer Staff HN 2",
                "staff_hn2@edriver.com", "0909777999", UserRole.DEALER_STAFF, dealer2);

        createUserIfAbsent("manager_dn", "manager123", "Dealer Manager DN",
                "manager_dn@edriver.com", "0909333444", UserRole.DEALER_MANAGER, dealer3);
        createUserIfAbsent("staff_dn1", "staff123", "Dealer Staff DN 1",
                "staff_dn1@edriver.com", "0909666111", UserRole.DEALER_STAFF, dealer3);

        createUserIfAbsent("manager_ct", "manager123", "Dealer Manager CT",
                "manager_ct@edriver.com", "0909444555", UserRole.DEALER_MANAGER, dealer4);
        createUserIfAbsent("staff_ct1", "staff123", "Dealer Staff CT 1",
                "staff_ct1@edriver.com", "0909666222", UserRole.DEALER_STAFF, dealer4);

        createUserIfAbsent("manager_hp", "manager123", "Dealer Manager HP",
                "manager_hp@edriver.com", "0909555660", UserRole.DEALER_MANAGER, dealer5);
        createUserIfAbsent("staff_hp1", "staff123", "Dealer Staff HP 1",
                "staff_hp1@edriver.com", "0909666333", UserRole.DEALER_STAFF, dealer5);

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
        v1.setPriceRetail(BigDecimal.valueOf(1_150_000_000.00));
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
        v2.setPriceRetail(BigDecimal.valueOf(1_350_000_000.00));
        v2.setStatus(VehicleStatus.DISCONTINUED);
        v2.setManufactureYear(2023);
        vehicleRepository.save(v2);

        Vehicle v3 = new Vehicle();
        v3.setModelName("E-Car B");
        v3.setVersion("Sport");
        v3.setColor("White");
        v3.setBatteryCapacityKwh(90);
        v3.setRangeKm(480);
        v3.setMaxSpeedKmh(175);
        v3.setChargingTimeHours(2.0f);
        v3.setSeatingCapacity(5);
        v3.setMotorPowerKw(190);
        v3.setWeightKg(1900);
        v3.setLengthMm(4600);
        v3.setWidthMm(1850);
        v3.setHeightMm(1620);
        v3.setPriceRetail(BigDecimal.valueOf(1_500_000_000.00));
        v3.setStatus(VehicleStatus.AVAILABLE);
        v3.setManufactureYear(2024);
        vehicleRepository.save(v3);

        Vehicle v4 = new Vehicle();
        v4.setModelName("E-Car B");
        v4.setVersion("Luxury");
        v4.setColor("Black");
        v4.setBatteryCapacityKwh(100);
        v4.setRangeKm(520);
        v4.setMaxSpeedKmh(180);
        v4.setChargingTimeHours(2.2f);
        v4.setSeatingCapacity(5);
        v4.setMotorPowerKw(210);
        v4.setWeightKg(1950);
        v4.setLengthMm(4650);
        v4.setWidthMm(1870);
        v4.setHeightMm(1650);
        v4.setPriceRetail(BigDecimal.valueOf(1_650_000_000.00));
        v4.setStatus(VehicleStatus.AVAILABLE);
        v4.setManufactureYear(2025);
        vehicleRepository.save(v4);

        Vehicle v5 = new Vehicle();
        v5.setModelName("E-Car C");
        v5.setVersion("Standard");
        v5.setColor("Silver");
        v5.setBatteryCapacityKwh(60);
        v5.setRangeKm(340);
        v5.setMaxSpeedKmh(135);
        v5.setChargingTimeHours(1.0f);
        v5.setSeatingCapacity(5);
        v5.setMotorPowerKw(120);
        v5.setWeightKg(1650);
        v5.setLengthMm(4300);
        v5.setWidthMm(1760);
        v5.setHeightMm(1550);
        v5.setPriceRetail(BigDecimal.valueOf(950_000_000.00));
        v5.setStatus(VehicleStatus.AVAILABLE);
        v5.setManufactureYear(2023);
        vehicleRepository.save(v5);

        Vehicle v6 = new Vehicle();
        v6.setModelName("E-Car C");
        v6.setVersion("Plus");
        v6.setColor("Green");
        v6.setBatteryCapacityKwh(75);
        v6.setRangeKm(400);
        v6.setMaxSpeedKmh(150);
        v6.setChargingTimeHours(1.5f);
        v6.setSeatingCapacity(5);
        v6.setMotorPowerKw(150);
        v6.setWeightKg(1750);
        v6.setLengthMm(4400);
        v6.setWidthMm(1780);
        v6.setHeightMm(1580);
        v6.setPriceRetail(BigDecimal.valueOf(1_100_000_000.00));
        v6.setStatus(VehicleStatus.AVAILABLE);
        v6.setManufactureYear(2024);
        vehicleRepository.save(v6);

        Vehicle v7 = new Vehicle();
        v7.setModelName("E-Car D");
        v7.setVersion("Standard");
        v7.setColor("Gray");
        v7.setBatteryCapacityKwh(85);
        v7.setRangeKm(460);
        v7.setMaxSpeedKmh(160);
        v7.setChargingTimeHours(1.8f);
        v7.setSeatingCapacity(7);
        v7.setMotorPowerKw(180);
        v7.setWeightKg(2000);
        v7.setLengthMm(4700);
        v7.setWidthMm(1880);
        v7.setHeightMm(1680);
        v7.setPriceRetail(BigDecimal.valueOf(1_400_000_000.00));
        v7.setStatus(VehicleStatus.AVAILABLE);
        v7.setManufactureYear(2022);
        vehicleRepository.save(v7);

        Vehicle v8 = new Vehicle();
        v8.setModelName("E-Car D");
        v8.setVersion("Limited Edition");
        v8.setColor("Yellow");
        v8.setBatteryCapacityKwh(100);
        v8.setRangeKm(550);
        v8.setMaxSpeedKmh(185);
        v8.setChargingTimeHours(2.5f);
        v8.setSeatingCapacity(7);
        v8.setMotorPowerKw(220);
        v8.setWeightKg(2100);
        v8.setLengthMm(4800);
        v8.setWidthMm(1900);
        v8.setHeightMm(1700);
        v8.setPriceRetail(BigDecimal.valueOf(1_750_000_000.00));
        v8.setStatus(VehicleStatus.AVAILABLE);
        v8.setManufactureYear(2025);
        vehicleRepository.save(v8);

        Vehicle v9 = new Vehicle();
        v9.setModelName("E-Car E");
        v9.setVersion("Standard");
        v9.setColor("Orange");
        v9.setBatteryCapacityKwh(50);
        v9.setRangeKm(280);
        v9.setMaxSpeedKmh(130);
        v9.setChargingTimeHours(0.9f);
        v9.setSeatingCapacity(4);
        v9.setMotorPowerKw(100);
        v9.setWeightKg(1500);
        v9.setLengthMm(4100);
        v9.setWidthMm(1720);
        v9.setHeightMm(1500);
        v9.setPriceRetail(BigDecimal.valueOf(780_000_000.00));
        v9.setStatus(VehicleStatus.AVAILABLE);
        v9.setManufactureYear(2023);
        vehicleRepository.save(v9);

        Vehicle v10 = new Vehicle();
        v10.setModelName("E-Car E");
        v10.setVersion("Premium");
        v10.setColor("White");
        v10.setBatteryCapacityKwh(65);
        v10.setRangeKm(350);
        v10.setMaxSpeedKmh(145);
        v10.setChargingTimeHours(1.3f);
        v10.setSeatingCapacity(4);
        v10.setMotorPowerKw(130);
        v10.setWeightKg(1600);
        v10.setLengthMm(4200);
        v10.setWidthMm(1740);
        v10.setHeightMm(1530);
        v10.setPriceRetail(BigDecimal.valueOf(920_000_000.00));
        v10.setStatus(VehicleStatus.AVAILABLE);
        v10.setManufactureYear(2024);
        vehicleRepository.save(v10);

        Vehicle v11 = new Vehicle();
        v11.setModelName("E-Car F");
        v11.setVersion("Plus");
        v11.setColor("Blue");
        v11.setBatteryCapacityKwh(110);
        v11.setRangeKm(580);
        v11.setMaxSpeedKmh(190);
        v11.setChargingTimeHours(2.8f);
        v11.setSeatingCapacity(6);
        v11.setMotorPowerKw(230);
        v11.setWeightKg(2050);
        v11.setLengthMm(4800);
        v11.setWidthMm(1900);
        v11.setHeightMm(1680);
        v11.setPriceRetail(BigDecimal.valueOf(1_750_000_000.00));
        v11.setStatus(VehicleStatus.DISCONTINUED);
        v11.setManufactureYear(2025);
        vehicleRepository.save(v11);

        Vehicle v12 = new Vehicle();
        v12.setModelName("E-Car G");
        v12.setVersion("Luxury");
        v12.setColor("Gray");
        v12.setBatteryCapacityKwh(120);
        v12.setRangeKm(620);
        v12.setMaxSpeedKmh(200);
        v12.setChargingTimeHours(3.0f);
        v12.setSeatingCapacity(7);
        v12.setMotorPowerKw(250);
        v12.setWeightKg(2200);
        v12.setLengthMm(4900);
        v12.setWidthMm(1950);
        v12.setHeightMm(1750);
        v12.setPriceRetail(BigDecimal.valueOf(1_900_000_000.00));
        v12.setStatus(VehicleStatus.AVAILABLE);
        v12.setManufactureYear(2025);
        vehicleRepository.save(v12);

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
    private void createUserIfAbsent(String username, String rawPass, String fullName,
                                    String email, String phone, UserRole role, Dealer dealer) {
        if (userRepository.findByUsername(username).isPresent()) return;

        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPass));
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPhone(phone);
        u.setRole(role);
        u.setDealer(dealer); // null cho ADMIN/EVM_STAFF; != null cho DEALER_*
        userRepository.save(u);
    }
}
