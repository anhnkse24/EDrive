package com.swp391.edrive.initializer;

import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.Gender;
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
    private final VehicleModelRepository vehicleModelRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final TestDriveRepository testDriveRepository;
    private final DealerInventoryRepository dealerInventoryRepository;
    private final VersionColorRepository versionColorRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // =======================
        // Dealers
        // =======================
        Dealer dealer1 = new Dealer();
        dealer1.setDealerCode("DLR001");
        dealer1.setDealerName("Edriver Center");
        dealer1.setAddress("123 Nguyen Van Troi, Phu Nhuan, TP. HCM");
        dealer1.setContactPerson("Nguyen Van A");
        dealer1.setPhone("0909123456");
        dealerRepository.save(dealer1);

        Dealer dealer2 = new Dealer();
        dealer2.setDealerCode("DLR002");
        dealer2.setDealerName("Edrive Hanoi Showroom");
        dealer2.setAddress("25 Tran Duy Hung, Cau Giay, Ha Noi");
        dealer2.setContactPerson("Tran Thi B");
        dealer2.setPhone("0912345678");
        dealerRepository.save(dealer2);

        Dealer dealer3 = new Dealer();
        dealer3.setDealerCode("DLR003");
        dealer3.setDealerName("Edrive Danang Center");
        dealer3.setAddress("78 Nguyen Van Linh, Hai Chau, Da Nang");
        dealer3.setContactPerson("Le Van C");
        dealer3.setPhone("0938765432");
        dealerRepository.save(dealer3);

        Dealer dealer4 = new Dealer();
        dealer4.setDealerCode("DLR004");
        dealer4.setDealerName("Edrive Can Tho Auto Mall");
        dealer4.setAddress("45 Cach Mang Thang 8, Ninh Kieu, Can Tho");
        dealer4.setContactPerson("Pham Thi D");
        dealer4.setPhone("0978123456");
        dealerRepository.save(dealer4);

        Dealer dealer5 = new Dealer();
        dealer5.setDealerCode("DLR005");
        dealer5.setDealerName("Edrive Hai Phong Premium Showroom");
        dealer5.setAddress("12 Le Hong Phong, Ngo Quyen, Hai Phong");
        dealer5.setContactPerson("Do Van E");
        dealer5.setPhone("0989988776");
        dealerRepository.save(dealer5);

        // =======================
        // Users (EVM + Dealers)
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
        // Vehicle Models / Versions / Colors
        // =======================

        // ---- E-Car A
        VehicleModel mA = new VehicleModel();
        mA.setModelName("E-Car A");
        mA.setDescription("Dòng xe A");
        mA.setImageUrl(null);
        mA.setVersions(new java.util.ArrayList<>());

        VehicleVersion aStd = newVersion(mA, "Standard",
                new BigDecimal("1150000000"),
                70, 380, 145, 1.5f, 5, 140,
                1750, 4450, 1800, 1580,
                2022, VehicleStatus.DISCONTINUED);
        addColor(aStd, "Red", "RED", null, null, null);
        addColor(aStd, "White", "WHT", new BigDecimal("10000000"), null, null);

        VehicleVersion aPre = newVersion(mA, "Premium",
                new BigDecimal("1350000000"),
                85, 450, 160, 1.8f, 5, 160,
                1850, 4500, 1820, 1600,
                2023, VehicleStatus.DISCONTINUED);
        addColor(aPre, "Blue", "BLU", null, null, null);
        addColor(aPre, "Pearl White", "WHT-P", null, new BigDecimal("1370000000"), null);

        vehicleModelRepository.save(mA); // cascade

        // ---- E-Car B
        VehicleModel mB = new VehicleModel();
        mB.setModelName("E-Car B");
        mB.setDescription("Dòng xe B");
        mB.setVersions(new java.util.ArrayList<>());

        VehicleVersion bSport = newVersion(mB, "Sport",
                new BigDecimal("1500000000"),
                90, 480, 175, 2.0f, 5, 190,
                1900, 4600, 1850, 1620,
                2024, VehicleStatus.AVAILABLE);
        addColor(bSport, "White", "WHT", null, null, null);

        VehicleVersion bLux = newVersion(mB, "Luxury",
                new BigDecimal("1650000000"),
                100, 520, 180, 2.2f, 5, 210,
                1950, 4650, 1870, 1650,
                2025, VehicleStatus.AVAILABLE);
        addColor(bLux, "Black", "BLK", null, null, null);
        addColor(bLux, "Yellow", "YEL", new BigDecimal("20000000"), null, null);
        vehicleModelRepository.save(mB);

        // ---- E-Car C
        VehicleModel mC = new VehicleModel();
        mC.setModelName("E-Car C");
        mC.setVersions(new java.util.ArrayList<>());

        VehicleVersion cStd = newVersion(mC, "Standard",
                new BigDecimal("950000000"),
                60, 340, 135, 1.0f, 5, 120,
                1650, 4300, 1760, 1550,
                2023, VehicleStatus.AVAILABLE);
        addColor(cStd, "Silver", "SLV", null, null, null);

        VehicleVersion cPlus = newVersion(mC, "Plus",
                new BigDecimal("1100000000"),
                75, 400, 150, 1.5f, 5, 150,
                1750, 4400, 1780, 1580,
                2024, VehicleStatus.AVAILABLE);
        addColor(cPlus, "Green", "GRN", null, null, null);

        vehicleModelRepository.save(mC);

        // ---- E-Car D
        VehicleModel mD = new VehicleModel();
        mD.setModelName("E-Car D");
        mD.setVersions(new java.util.ArrayList<>());

        VehicleVersion dStd = newVersion(mD, "Standard",
                new BigDecimal("1400000000"),
                85, 460, 160, 1.8f, 7, 180,
                2000, 4700, 1880, 1680,
                2022, VehicleStatus.AVAILABLE);
        addColor(dStd, "Gray", "GRY", null, null, null);

        VehicleVersion dLE = newVersion(mD, "Limited Edition",
                new BigDecimal("1750000000"),
                100, 550, 185, 2.5f, 7, 220,
                2100, 4800, 1900, 1700,
                2025, VehicleStatus.AVAILABLE);
        addColor(dLE, "Yellow", "YEL", null, null, null);

        vehicleModelRepository.save(mD);

        // ---- E-Car E
        VehicleModel mE = new VehicleModel();
        mE.setModelName("E-Car E");
        mE.setVersions(new java.util.ArrayList<>());

        VehicleVersion eStd = newVersion(mE, "Standard",
                new BigDecimal("780000000"),
                50, 280, 130, 0.9f, 4, 100,
                1500, 4100, 1720, 1500,
                2023, VehicleStatus.AVAILABLE);
        addColor(eStd, "Orange", "ORG", null, null, null);

        VehicleVersion ePre = newVersion(mE, "Premium",
                new BigDecimal("920000000"),
                65, 350, 145, 1.3f, 4, 130,
                1600, 4200, 1740, 1530,
                2024, VehicleStatus.AVAILABLE);
        addColor(ePre, "White", "WHT", null, null, null);

        vehicleModelRepository.save(mE);

        // ---- E-Car F
        VehicleModel mF = new VehicleModel();
        mF.setModelName("E-Car F");
        mF.setVersions(new java.util.ArrayList<>());

        VehicleVersion fPlus = newVersion(mF, "Plus",
                new BigDecimal("1750000000"),
                110, 580, 190, 2.8f, 6, 230,
                2050, 4800, 1900, 1680,
                2025, VehicleStatus.DISCONTINUED);
        addColor(fPlus, "Blue", "BLU", null, null, null);

        vehicleModelRepository.save(mF);

        // ---- E-Car G
        VehicleModel mG = new VehicleModel();
        mG.setModelName("E-Car G");
        mG.setVersions(new java.util.ArrayList<>());

        VehicleVersion gLux = newVersion(mG, "Luxury",
                new BigDecimal("1900000000"),
                120, 620, 200, 3.0f, 7, 250,
                2200, 4900, 1950, 1750,
                2025, VehicleStatus.AVAILABLE);
        addColor(gLux, "Gray", "GRY", null, null, null);

        vehicleModelRepository.save(mG);

        // =======================
        // Customers  (Gender enum!)
        // =======================
        Customer c1 = new Customer();
        c1.setFullName("Nguyễn Minh Hòa");
        c1.setDob(LocalDate.of(1998, 5, 12));
        c1.setGender(Gender.NAM);  // <-- enum
        c1.setEmail("hoa.nguyen@example.com");
        c1.setPhone("0909123456");
        c1.setAddress("12 Lê Lợi, Q1, TP.HCM");
        c1.setIdCardNo("079123456789");
        customerRepository.save(c1);

        Customer c2 = new Customer();
        c2.setFullName("Trần Thu Hà");
        c2.setDob(LocalDate.of(1996, 11, 3));
        c2.setGender(Gender.NU);   // <-- enum
        c2.setEmail("ha.tran@example.com");
        c2.setPhone("0911222333");
        c2.setAddress("45 Hai Bà Trưng, Q1, TP.HCM");
        c2.setIdCardNo("031234567");
        customerRepository.save(c2);

        Customer c3 = new Customer();
        c3.setFullName("Phạm Quang Khải");
        c3.setDob(LocalDate.of(1993, 2, 20));
        c3.setGender(Gender.KHAC); // <-- enum
        c3.setEmail("khai.pham@example.com");
        c3.setPhone("0933666777");
        c3.setAddress("99 Phạm Văn Đồng, Thủ Đức, TP.HCM");
        c3.setIdCardNo("123456789012");
        customerRepository.save(c3);

        // =======================
        // Helper: round to 00/30
        // =======================
        Function<LocalDateTime, LocalDateTime> roundToSlot = dt -> {
            int m = dt.getMinute();
            if (m == 0 || m == 30) return dt.withSecond(0).withNano(0);
            if (m < 30) return dt.withMinute(30).withSecond(0).withNano(0);
            return dt.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        };
        LocalTime CLOSE = LocalTime.of(17, 30);
        Duration SLOT = Duration.ofMinutes(30);

        // =======================
        // TestDrives
        // =======================
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        LocalDate threeDaysAgo = today.minusDays(3);

        // --- Future (tomorrow)
        TestDrive td1 = new TestDrive();
        td1.setCustomer(c1);
        td1.setDealer(dealer1);
        td1.setVersion(aStd);
        td1.setScheduledAt(LocalDateTime.of(tomorrow, LocalTime.of(9, 0)));
        td1.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(td1);

        TestDrive td2 = new TestDrive();
        td2.setCustomer(c2);
        td2.setDealer(dealer1);
        td2.setVersion(aPre);
        td2.setScheduledAt(LocalDateTime.of(tomorrow, LocalTime.of(9, 30)));
        td2.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(td2);

        TestDrive td3 = new TestDrive();
        td3.setCustomer(c3);
        td3.setDealer(dealer1);
        td3.setVersion(aStd);
        td3.setScheduledAt(LocalDateTime.of(tomorrow, LocalTime.of(14, 30)));
        td3.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(td3);

        // --- Today (+3h)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime slot1 = roundToSlot.apply(now.plusHours(3));
        LocalDateTime slot2 = slot1.plusMinutes(30);
        if (slot2.toLocalTime().isAfter(CLOSE.minus(SLOT))) {
            slot2 = LocalDateTime.of(today, CLOSE.minus(SLOT));
        }

        TestDrive tdToday1 = new TestDrive();
        tdToday1.setCustomer(c2);
        tdToday1.setDealer(dealer1);
        tdToday1.setVersion(aStd);
        tdToday1.setScheduledAt(slot1);
        tdToday1.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(tdToday1);

        TestDrive tdToday2 = new TestDrive();
        tdToday2.setCustomer(c3);
        tdToday2.setDealer(dealer1);
        tdToday2.setVersion(aPre);
        tdToday2.setScheduledAt(slot2);
        tdToday2.setStatus(TestDriveStatus.PENDING);
        testDriveRepository.save(tdToday2);

        // --- Past
        TestDrive tdPast1 = new TestDrive();
        tdPast1.setCustomer(c1); tdPast1.setDealer(dealer1); tdPast1.setVersion(aStd);
        tdPast1.setScheduledAt(LocalDateTime.of(yesterday, LocalTime.of(10, 0)));
        tdPast1.setStatus(TestDriveStatus.COMPLETED);
        testDriveRepository.save(tdPast1);

        TestDrive tdPast2 = new TestDrive();
        tdPast2.setCustomer(c2); tdPast2.setDealer(dealer1); tdPast2.setVersion(aPre);
        tdPast2.setScheduledAt(LocalDateTime.of(yesterday, LocalTime.of(10, 30)));
        tdPast2.setStatus(TestDriveStatus.COMPLETED);
        testDriveRepository.save(tdPast2);

        TestDrive tdPast3 = new TestDrive();
        tdPast3.setCustomer(c3); tdPast3.setDealer(dealer1); tdPast3.setVersion(aStd);
        tdPast3.setScheduledAt(LocalDateTime.of(threeDaysAgo, LocalTime.of(15, 0)));
        tdPast3.setStatus(TestDriveStatus.CANCELLED);
        testDriveRepository.save(tdPast3);

        TestDrive tdPast4 = new TestDrive();
        tdPast4.setCustomer(c1); tdPast4.setDealer(dealer1); tdPast4.setVersion(aPre);
        tdPast4.setScheduledAt(LocalDateTime.of(threeDaysAgo, LocalTime.of(15, 30)));
        tdPast4.setStatus(TestDriveStatus.COMPLETED);
        testDriveRepository.save(tdPast4);

        // --- Example: specific color
        VersionColor aStdRed  = aStd.getColors().stream()
                .filter(c -> "RED".equals(c.getColorCode())).findFirst().orElse(null);
        if (aStdRed != null) {
            TestDrive tdColor = new TestDrive();
            tdColor.setCustomer(c1);
            tdColor.setDealer(dealer1);
            tdColor.setVersion(aStd);
            tdColor.setVersionColor(aStdRed);
            tdColor.setScheduledAt(LocalDateTime.of(tomorrow, LocalTime.of(11, 0)));
            tdColor.setStatus(TestDriveStatus.PENDING);
            testDriveRepository.save(tdColor);
        }

        upsertInventory(dealer1, findColorByCode(aStd, "RED"), 2, 0);
        upsertInventory(dealer1, findColorByCode(aStd, "WHT"), 1, 0);
        upsertInventory(dealer2, findColorByCode(bLux, "BLK"), 1, 0);
        upsertInventory(dealer2, findColorByCode(bLux, "YEL"), 1, 0);
        upsertInventory(dealer3, findColorByCode(aPre, "BLU"), 1, 0);
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

    private VehicleVersion newVersion(VehicleModel model, String versionName,
                                      BigDecimal basePrice,
                                      Integer batteryKwh, Integer rangeKm, Integer maxSpeedKmh,
                                      Float chargeHours, Integer seats, Integer powerKw,
                                      Integer weightKg, Integer lenMm, Integer widthMm, Integer heightMm,
                                      Integer year, VehicleStatus status) {
        VehicleVersion v = new VehicleVersion();
        v.setModel(model);
        v.setVersionName(versionName);
        v.setBasePrice(basePrice);
        v.setBatteryCapacityKwh(batteryKwh);
        v.setRangeKm(rangeKm);
        v.setMaxSpeedKmh(maxSpeedKmh);
        v.setChargingTimeHours(chargeHours);
        v.setSeatingCapacity(seats);
        v.setMotorPowerKw(powerKw);
        v.setWeightKg(weightKg);
        v.setLengthMm(lenMm);
        v.setWidthMm(widthMm);
        v.setHeightMm(heightMm);
        v.setManufactureYear(year);
        v.setStatus(status);
        model.getVersions().add(v); // cascade từ model
        return v;
    }

    private VersionColor addColor(VehicleVersion version, String name, String code,
                                  BigDecimal priceDelta, BigDecimal priceOverride,
                                  String imageUrl) {
        VersionColor c = new VersionColor();
        c.setVersion(version);
        c.setColorName(name);
        c.setColorCode(code);
        c.setPriceDelta(priceDelta);
        c.setPriceOverride(priceOverride);
        c.setImageUrl(imageUrl);
        c.setIsActive(true);
        version.getColors().add(c); // cascade từ version
        return c;
    }

    private VersionColor findColorByCode(VehicleVersion version, String colorCode) {
        return version.getColors().stream()
                .filter(c -> c.getColorCode() != null
                        && c.getColorCode().equalsIgnoreCase(colorCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Color code '" + colorCode + "' not found in version " + version.getVersionName()
                ));
    }

    private DealerInventory upsertInventory(Dealer dealer, VersionColor color, int onHand, int reserved) {
        if (onHand < 0 || reserved < 0) {
            throw new IllegalArgumentException("onHand/reserved must be >= 0");
        }

        DealerInventory inv = dealerInventoryRepository
                .findByDealer_DealerIdAndVersionColor_Id(dealer.getDealerId(), color.getId())
                .orElseGet(() -> {
                    DealerInventory x = new DealerInventory();
                    x.setDealer(dealer);
                    x.setVersionColor(color);
                    return x;
                });

        inv.setOnHand(onHand);
        inv.setReserved(reserved);

        return dealerInventoryRepository.save(inv);
    }
}
