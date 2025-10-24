package com.swp391.edrive.initializer;

import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.*;
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
import java.util.List;
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
    private final PromotionRepository promotionRepository;
    private final InventoryRepository inventoryRepository;


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // =======================
        // Tạo Đại Lý
        // =======================
        Dealer dealer1 = new Dealer();
        dealer1.setDealerName("Edriver Center");
        dealer1.setHouseNumberAndStreet("123 Nguyen Van Troi");
        dealer1.setWardOrCommune("Phu Nhuan");
        dealer1.setDistrict("Phu Nhuan");
        dealer1.setProvinceOrCity("Ho Chi Minh City");
        dealer1.setContactPerson("Nguyen Van A");
        dealer1.setPhone("0909123456");
        dealerRepository.save(dealer1);

        Dealer dealer2 = new Dealer();
        dealer2.setDealerName("Edrive Hanoi Showroom");
        dealer2.setHouseNumberAndStreet("25 Tran Duy Hung");
        dealer2.setWardOrCommune("Trung Hoa");
        dealer2.setDistrict("Cau Giay");
        dealer2.setProvinceOrCity("Ha Noi");
        dealer2.setContactPerson("Tran Thi B");
        dealer2.setPhone("0912345678");
        dealerRepository.save(dealer2);

        Dealer dealer3 = new Dealer();
        dealer3.setDealerName("Edrive Danang Center");
        dealer3.setHouseNumberAndStreet("78 Nguyen Van Linh");
        dealer3.setWardOrCommune("Hai Chau");
        dealer3.setDistrict("Hai Chau");
        dealer3.setProvinceOrCity("Da Nang");
        dealer3.setContactPerson("Le Van C");
        dealer3.setPhone("0938765432");
        dealerRepository.save(dealer3);

        Dealer dealer4 = new Dealer();
        dealer4.setDealerName("Edrive Can Tho Auto Mall");
        dealer4.setHouseNumberAndStreet("45 Cach Mang Thang 8");
        dealer4.setWardOrCommune("Ninh Kieu");
        dealer4.setDistrict("Ninh Kieu");
        dealer4.setProvinceOrCity("Can Tho");
        dealer4.setContactPerson("Pham Thi D");
        dealer4.setPhone("0978123456");
        dealerRepository.save(dealer4);

        Dealer dealer5 = new Dealer();
        dealer5.setDealerName("Edrive Hai Phong Premium Showroom");
        dealer5.setHouseNumberAndStreet("12 Le Hong Phong");
        dealer5.setWardOrCommune("Ngo Quyen");
        dealer5.setDistrict("Ngo Quyen");
        dealer5.setProvinceOrCity("Hai Phong");
        dealer5.setContactPerson("Do Van E");
        dealer5.setPhone("0989988776");
        dealerRepository.save(dealer5);Dealer dealer6 = new Dealer();
        dealer6.setDealerName("Edrive Nha Trang Showroom");
        dealer6.setHouseNumberAndStreet("99 Tran Phu");
        dealer6.setWardOrCommune("Lộc Thọ");
        dealer6.setDistrict("Nha Trang");
        dealer6.setProvinceOrCity("Khanh Hoa");
        dealer6.setContactPerson("Nguyen Thi F");
        dealer6.setPhone("0909888777");
        dealerRepository.save(dealer6);

// --- Tạo user cho đại lý (Manager + Staff)
        createUserIfAbsent("manager_nt", "manager123", "Dealer Manager NT",
                "manager_nt@edriver.com", "0909888778", UserRole.DEALER_MANAGER, dealer6);
        createUserIfAbsent("staff_nt1", "staff123", "Dealer Staff NT 1",
                "staff_nt1@edriver.com", "0909888779", UserRole.DEALER_STAFF, dealer6);

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
        v1.setPriceRetail(BigDecimal.valueOf(1_150_000.00));
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
//        Function<LocalDateTime, LocalDateTime> roundToSlot = dt -> {
//            int m = dt.getMinute();
//            if (m == 0 || m == 30) return dt.withSecond(0).withNano(0);
//            if (m < 30) return dt.withMinute(30).withSecond(0).withNano(0);
//            return dt.plusHours(1).withMinute(0).withSecond(0).withNano(0);
//        };
//
//        LocalTime OPEN = LocalTime.of(8, 0);
//        LocalTime CLOSE = LocalTime.of(17, 30);
//        Duration SLOT = Duration.ofMinutes(30);
//
//        // =======================
//        // TestDrives
//        // =======================
//        LocalDate today = LocalDate.now();
//        LocalDate tomorrow = today.plusDays(1);
//        LocalDate yesterday = today.minusDays(1);
//        LocalDate threeDaysAgo = today.minusDays(3);
//
//        // --- Lịch hẹn tương lai (ngày mai)
//        TestDrive td1 = new TestDrive(c1, dealer1, v1, LocalDateTime.of(tomorrow, LocalTime.of(9, 0)), TestDriveStatus.PENDING);
//        testDriveRepository.save(td1);
//        TestDrive td2 = new TestDrive(c2, dealer1, v2, LocalDateTime.of(tomorrow, LocalTime.of(9, 30)), TestDriveStatus.PENDING);
//        testDriveRepository.save(td2);
//        TestDrive td3 = new TestDrive(c3, dealer1, v1, LocalDateTime.of(tomorrow, LocalTime.of(14, 30)), TestDriveStatus.PENDING);
//        testDriveRepository.save(td3);
//
//        // --- Lịch hẹn hôm nay (hiện tại + 3 tiếng)
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime slot1 = roundToSlot.apply(now.plusHours(3));
//        LocalDateTime slot2 = slot1.plusMinutes(30);
//        if (slot2.toLocalTime().isAfter(CLOSE.minus(SLOT))) {
//            slot2 = LocalDateTime.of(today, CLOSE.minus(SLOT));
//        }
//
//        TestDrive tdToday1 = new TestDrive(c2, dealer1, v1, slot1, TestDriveStatus.PENDING);
//        testDriveRepository.save(tdToday1);
//        TestDrive tdToday2 = new TestDrive(c3, dealer1, v2, slot2, TestDriveStatus.PENDING);
//        testDriveRepository.save(tdToday2);
//
//        // --- Lịch hẹn đã qua (hôm qua & 3 ngày trước)
//        TestDrive tdPast1 = new TestDrive(c1, dealer1, v1, LocalDateTime.of(yesterday, LocalTime.of(10, 0)), TestDriveStatus.COMPLETED);
//        testDriveRepository.save(tdPast1);
//        TestDrive tdPast2 = new TestDrive(c2, dealer1, v2, LocalDateTime.of(yesterday, LocalTime.of(10, 30)), TestDriveStatus.COMPLETED);
//        testDriveRepository.save(tdPast2);
//        TestDrive tdPast3 = new TestDrive(c3, dealer1, v1, LocalDateTime.of(threeDaysAgo, LocalTime.of(15, 0)), TestDriveStatus.CANCELLED);
//        testDriveRepository.save(tdPast3);
//        TestDrive tdPast4 = new TestDrive(c1, dealer1, v2, LocalDateTime.of(threeDaysAgo, LocalTime.of(15, 30)), TestDriveStatus.COMPLETED);
//        testDriveRepository.save(tdPast4);

        // =======================
        // Promotions (sample data)
        // =======================
        Promotion promo1 = new Promotion();
        promo1.setTitle("Giảm 10% cho dòng E-Car B");
        promo1.setDescription("Chương trình khuyến mãi mùa hè - giảm 10% giá bán cho tất cả phiên bản của dòng E-Car B.");
        promo1.setDiscountType(DiscountType.PERCENTAGE);
        promo1.setDiscountValue(10.0);
        promo1.setStartDate(LocalDate.now().minusDays(5));
        promo1.setEndDate(LocalDate.now().plusDays(20));
        promo1.setApplicableTo(PromoTarget.ALL);
        promo1.getVehicles().add(v3); // E-Car B Sport
        promo1.getVehicles().add(v4); // E-Car B Luxury

        Promotion promo2 = new Promotion();
        promo2.setTitle("Giảm 50 triệu cho E-Car C Plus");
        promo2.setDescription("Ưu đãi đặc biệt khi mua E-Car C Plus trong tháng này – giảm trực tiếp 50 triệu đồng.");
        promo2.setDiscountType(DiscountType.FIXED_AMOUNT);
        promo2.setDiscountValue(50_000_000.0);
        promo2.setStartDate(LocalDate.now());
        promo2.setEndDate(LocalDate.now().plusDays(30));
        promo2.setApplicableTo(PromoTarget.ALL);
        promo2.getVehicles().add(v6);

        Promotion promo3 = new Promotion();
        promo3.setTitle("Ưu đãi sinh nhật E-Drive");
        promo3.setDescription("Giảm 5% cho tất cả các mẫu xe trong dịp sinh nhật thương hiệu E-Drive.");
        promo3.setDiscountType(DiscountType.PERCENTAGE);
        promo3.setDiscountValue(5.0);
        promo3.setStartDate(LocalDate.now().minusDays(10));
        promo3.setEndDate(LocalDate.now().plusDays(10));
        promo3.setApplicableTo(PromoTarget.ALL);

        Promotion promo4 = new Promotion();
        promo4.setTitle("Tặng gói bảo dưỡng 2 năm cho khách hàng E-Car D");
        promo4.setDescription("Mua xe E-Car D trong thời gian khuyến mãi sẽ được tặng gói bảo dưỡng miễn phí 2 năm.");
        promo4.setDiscountType(DiscountType.FIXED_AMOUNT);
        promo4.setDiscountValue(0.0);
        promo4.setStartDate(LocalDate.now().plusDays(1));
        promo4.setEndDate(LocalDate.now().plusDays(45));
        promo4.setApplicableTo(PromoTarget.ALL);
        promo4.getVehicles().add(v7);
        promo4.getVehicles().add(v8);

        Promotion promo5 = new Promotion();
        promo5.setTitle("Giảm giá 7% cho khách hàng thân thiết");
        promo5.setDescription("Áp dụng cho khách hàng đã từng đặt lịch lái thử hoặc mua xe trước đây.");
        promo5.setDiscountType(DiscountType.PERCENTAGE);
        promo5.setDiscountValue(7.0);
        promo5.setStartDate(LocalDate.now().minusDays(3));
        promo5.setEndDate(LocalDate.now().plusDays(25));
        promo5.setApplicableTo(PromoTarget.CUSTOMER);

        promotionRepository.saveAll(List.of(promo1, promo2, promo3, promo4, promo5));


        // =======================
        // TestDrives (dữ liệu mẫu)
        // =======================
        LocalDateTime now = LocalDateTime.now();

        TestDrive td1 = new TestDrive();
        td1.setCustomer(c1);
        td1.setDealer(dealer1);
        td1.setVehicle(v3);
        td1.setScheduleDatetime(now.plusDays(1).withHour(9).withMinute(0));
        td1.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(td1);

        TestDrive td2 = new TestDrive();
        td2.setCustomer(c2);
        td2.setDealer(dealer1);
        td2.setVehicle(v4);
        td2.setScheduleDatetime(now.plusDays(2).withHour(10).withMinute(30));
        td2.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(td2);

        TestDrive td3 = new TestDrive();
        td3.setCustomer(c3);
        td3.setDealer(dealer2);
        td3.setVehicle(v6);
        td3.setScheduleDatetime(now.minusDays(1).withHour(14).withMinute(0));
        td3.setStatus(TestDriveStatus.COMPLETED);
        td3.setCompletedAt(now.minusDays(1).withHour(15));
        testDriveRepository.save(td3);

        TestDrive td4 = new TestDrive();
        td4.setCustomer(c1);
        td4.setDealer(dealer3);
        td4.setVehicle(v7);
        td4.setScheduleDatetime(now.minusDays(2).withHour(10));
        td4.setStatus(TestDriveStatus.CANCELLED);
        td4.setCancelReason("Khách bận công tác");
        testDriveRepository.save(td4);

        TestDrive td5 = new TestDrive();
        td5.setCustomer(c2);
        td5.setDealer(dealer5);
        td5.setVehicle(v10);
        td5.setScheduleDatetime(now.plusDays(3).withHour(16));
        td5.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(td5);

        // =======================
        // Inventory (số lượng xe mỗi đại lý có trong kho)
        // =======================
        Inventory inv1 = new Inventory();
        inv1.setDealer(dealer1);
        inv1.setVehicle(v3);
        inv1.setQuantity(8);
        inv1.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inv1);

        Inventory inv2 = new Inventory();
        inv2.setDealer(dealer1);
        inv2.setVehicle(v4);
        inv2.setQuantity(5);
        inv2.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inv2);

        Inventory inv3 = new Inventory();
        inv3.setDealer(dealer2);
        inv3.setVehicle(v5);
        inv3.setQuantity(10);
        inv3.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inv3);

        Inventory inv4 = new Inventory();
        inv4.setDealer(dealer2);
        inv4.setVehicle(v6);
        inv4.setQuantity(7);
        inv4.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inv4);

        Inventory inv5 = new Inventory();
        inv5.setDealer(dealer3);
        inv5.setVehicle(v7);
        inv5.setQuantity(6);
        inv5.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inv5);

        Inventory inv6 = new Inventory();
        inv6.setDealer(dealer4);
        inv6.setVehicle(v9);
        inv6.setQuantity(12);
        inv6.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inv6);

        Inventory inv7 = new Inventory();
        inv7.setDealer(dealer5);
        inv7.setVehicle(v10);
        inv7.setQuantity(4);
        inv7.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inv7);

        Inventory inv8 = new Inventory();
        inv8.setDealer(dealer5);
        inv8.setVehicle(v12);
        inv8.setQuantity(2);
        inv8.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inv8);

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
        u.setDealer(dealer);
        userRepository.save(u);
    }
}
