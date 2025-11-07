package com.swp391.edrive.initializer;

import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.DiscountType;
import com.swp391.edrive.enums.PromoTarget;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ManufacturerInventoryRepository manufacturerInventoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DealerRepository dealerRepository;
    private final PasswordEncoder passwordEncoder;
    private final PromotionRepository promotionRepository;
    private final TestDriveRepository testDriveRepository;
    private final CustomerRepository customerRepository;
    private final OrderCustomerRepository orderCustomerRepository;
    private final StatusOrderCustomerRepository statusOrderCustomerRepository;
    private final FeedbackRepository feedbackRepository;
    private final ColorRepository colorRepository;



    @Override
    public void run(String... args) throws Exception {
        // ========== 1) SEED ROLES & USERS (chạy luôn, không phụ thuộc dữ liệu khác) ==========
        // 1.1 Roles
        Role adminRole          = roleRepository.findById("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").description("System Administrator").build()));
        Role dealerManagerRole  = roleRepository.findById("DEALER_MANAGER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("DEALER_MANAGER").description("Dealer Manager").build()));
        Role dealerStaffRole    = roleRepository.findById("DEALER_STAFF")
                .orElseGet(() -> roleRepository.save(Role.builder().name("DEALER_STAFF").description("Dealer Staff").build()));
        Role evmStaffRole       = roleRepository.findById("EVM_STAFF")
                .orElseGet(() -> roleRepository.save(Role.builder().name("EVM_STAFF").description("E-Drive Manufacturer Staff").build()));


        // 1.2 Admin user (nếu chưa có)
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Admin@123")) // đổi sau khi đăng nhập
                    .fullName("System Administrator")
                    .email("admin@edrive.local")
                    .phone("0000000000")
                    .isVerify(true)
                    .build();
            admin.setRoles(new HashSet<>(Set.of(adminRole)));
            userRepository.save(admin);
            System.out.println("✅ Seeded admin: admin / Admin@123");
        }

        // 1.3 EVM Staff (nhân viên hãng)
        if (!userRepository.existsByUsername("evm1")) {
            User evm1 = User.builder()
                    .username("evm1")
                    .password(passwordEncoder.encode("Evm@123")) // đổi sau khi đăng nhập
                    .fullName("EVM Staff #1")
                    .email("evm1@edrive.local")
                    .phone("0900001001")
                    .isVerify(true)
                    .build();
            evm1.setRoles(new HashSet<>(Set.of(evmStaffRole)));
            userRepository.save(evm1);
            System.out.println("✅ Seeded EVM staff: evm1 / Evm@123");
        }

        // 1.3 Dealers & Dealer users
        List<Dealer> dealersInDb = dealerRepository.findAll();
        if (dealersInDb.isEmpty()) {
            List<Dealer> seedDealers = new ArrayList<>();
            seedDealers.add(makeDealer("E-Drive Hà Nội", "123 Trần Duy Hưng", "Trung Hòa", "Cầu Giấy", "Hà Nội", "Liên hệ 1", "0900000001"));
            seedDealers.add(makeDealer("E-Drive Hồ Chí Minh", "45 Lê Lợi", "Bến Nghé", "Quận 1", "Hồ Chí Minh", "Liên hệ 2", "0900000002"));
            seedDealers.add(makeDealer("E-Drive Đà Nẵng", "99 Nguyễn Văn Linh", "Hải Châu 1", "Hải Châu", "Đà Nẵng", "Liên hệ 3", "0900000003"));
            seedDealers.add(makeDealer("E-Drive Hải Phòng", "12 Điện Biên Phủ", "Minh Khai", "Hồng Bàng", "Hải Phòng", "Liên hệ 4", "0900000004"));
            seedDealers.add(makeDealer("E-Drive Cần Thơ", "8 Mậu Thân", "Xuân Khánh", "Ninh Kiều", "Cần Thơ", "Liên hệ 5", "0900000005"));
            seedDealers.add(makeDealer("E-Drive Nha Trang", "27 Trần Phú", "Lộc Thọ", "Nha Trang", "Khánh Hòa", "Liên hệ 6", "0900000006"));
            seedDealers.add(makeDealer("E-Drive Biên Hòa", "18 Phạm Văn Thuận", "Tân Tiến", "Biên Hòa", "Đồng Nai", "Liên hệ 7", "0900000007"));
            seedDealers.add(makeDealer("E-Drive Vinh", "56 Trường Chinh", "Trung Đô", "Vinh", "Nghệ An", "Liên hệ 8", "0900000008"));
            seedDealers.add(makeDealer("E-Drive Buôn Ma Thuột", "10 Nguyễn Tất Thành", "Tân Lợi", "Buôn Ma Thuột", "Đắk Lắk", "Liên hệ 9", "0900000009"));
            seedDealers.add(makeDealer("E-Drive Thanh Hóa", "20 Hạc Thành", "Tân Sơn", "Thanh Hóa", "Thanh Hóa", "Liên hệ 10", "0900000010"));
            dealersInDb = dealerRepository.saveAll(seedDealers);
            System.out.println("✅ Đã khởi tạo " + dealersInDb.size() + " dealers");
        }

        for (int i = 0; i < dealersInDb.size(); i++) {
            Dealer d = dealersInDb.get(i);
            String base = "d" + (i + 1); // d1, d2,...

            String managerUsername = base + "_manager";
            if (!userRepository.existsByUsername(managerUsername)) {
                User manager = User.builder()
                        .username(managerUsername)
                        .password(passwordEncoder.encode("Dealer@123")) // đổi sau khi đăng nhập
                        .fullName(d.getDealerName() + " Manager")
                        .email(managerUsername + "@edrive.local")
                        .phone(d.getPhone() != null ? d.getPhone() : ("09" + String.format("%08d", i + 1)))
                        .dealer(d)
                        .isVerify(true)
                        .build();
                manager.setRoles(new HashSet<>(Set.of(dealerManagerRole)));
                userRepository.save(manager);
                System.out.println("✅ Seeded dealer manager: " + managerUsername + " / Dealer@123");
            }

            String staffUsername = base + "_staff";
            if (!userRepository.existsByUsername(staffUsername)) {
                User staff = User.builder()
                        .username(staffUsername)
                        .password(passwordEncoder.encode("Dealer@123"))
                        .fullName(d.getDealerName() + " Staff")
                        .email(staffUsername + "@edrive.local")
                        .phone("09" + String.format("%08d", 1000 + i))
                        .dealer(d)
                        .isVerify(true)
                        .build();
                staff.setRoles(new HashSet<>(Set.of(dealerStaffRole)));
                userRepository.save(staff);
                System.out.println("✅ Seeded dealer staff: " + staffUsername + " / Dealer@123");
            }
        }

        // Khởi tạo Manufacturers
        Manufacturer edrive = new Manufacturer();
        edrive.setManufacturerName("EDrive");
        edrive.setAddress("Khu Công nghệ cao Hòa Lạc, Huyện Thạch Thất, Hà Nội");
        edrive.setContactPerson("Nguyễn Văn A");
        edrive.setPhone("0243-123-4567");

        List<Manufacturer> manufacturers = manufacturerRepository.saveAll(Arrays.asList(edrive));
        System.out.println("✅ Đã khởi tạo " + manufacturers.size() + " manufacturers");

// ====== Seed Colors (dùng chung) ======
        Color red    = upsertColor("Đỏ",      "#FF0000");
        Color black  = upsertColor("Đen",     "#000000");
        Color white  = upsertColor("Trắng",   "#FFFFFF");
        Color blue   = upsertColor("Xanh",    "#0055FF");
        Color silver = upsertColor("Bạc",     "#C0C0C0");
        Color gray   = upsertColor("Xám",     "#808080");
        Color cyan   = upsertColor("Xanh Lam","#1E90FF");
        Color green  = upsertColor("Xanh lá", "#24K07A");
        Color yellow  = upsertColor("Vàng", "#FDFD49");

// ====== Khởi tạo Vehicles cho VinFast ======
        Vehicle vf8_silver = new Vehicle();
        vf8_silver.setManufacturer(edrive);
        vf8_silver.setModelName("VF 8");
        vf8_silver.setVersion("Standard");
        vf8_silver.setColor(silver);
        vf8_silver.setBatteryCapacityKwh(40);
        vf8_silver.setRangeKm(250);
        vf8_silver.setMaxSpeedKmh(140);
        vf8_silver.setChargingTimeHours(4.5f);
        vf8_silver.setSeatingCapacity(5);
        vf8_silver.setMotorPowerKw(100);
        vf8_silver.setWeightKg(1600);
        vf8_silver.setLengthMm(4200);
        vf8_silver.setWidthMm(1800);
        vf8_silver.setHeightMm(1500);
        vf8_silver.setPriceRetail(new BigDecimal("2000000000"));
        vf8_silver.setManufactureYear(2024);
        vf8_silver.setImageUrl("https://vinfast-thaodien.com/wp-content/uploads/2025/02/vf8eco.webp");
        vf8_silver.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vf8_red = new Vehicle();
        vf8_red.setManufacturer(edrive);
        vf8_red.setModelName("VF 8");
        vf8_red.setVersion("Standard");
        vf8_red.setColor(red);
        vf8_red.setBatteryCapacityKwh(40);
        vf8_red.setRangeKm(250);
        vf8_red.setMaxSpeedKmh(140);
        vf8_red.setChargingTimeHours(4.5f);
        vf8_red.setSeatingCapacity(5);
        vf8_red.setMotorPowerKw(100);
        vf8_red.setWeightKg(1600);
        vf8_red.setLengthMm(4200);
        vf8_red.setWidthMm(1800);
        vf8_red.setHeightMm(1500);
        vf8_red.setPriceRetail(new BigDecimal("2000000000"));
        vf8_red.setManufactureYear(2024);
        vf8_red.setImageUrl("https://vinfast-thaodien.com/wp-content/uploads/2025/02/vf8plus.webp");
        vf8_red.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vf9_blue = new Vehicle();
        vf9_blue.setManufacturer(edrive);
        vf9_blue.setModelName("VF 9");
        vf9_blue.setVersion("Plus");
        vf9_blue.setColor(blue);
        vf9_blue.setBatteryCapacityKwh(123);
        vf9_blue.setRangeKm(594);
        vf9_blue.setMaxSpeedKmh(200);
        vf9_blue.setChargingTimeHours(9.0f);
        vf9_blue.setSeatingCapacity(7);
        vf9_blue.setMotorPowerKw(300);
        vf9_blue.setWeightKg(2400);
        vf9_blue.setLengthMm(5123);
        vf9_blue.setWidthMm(1976);
        vf9_blue.setHeightMm(1750);
        vf9_blue.setPriceRetail(new BigDecimal("2000"));
        vf9_blue.setManufactureYear(2024);
        vf9_blue.setImageUrl("https://vinfasthadong.com.vn/wp-content/uploads/2023/10/vinfast-vf9_blue-blue.jpg");
        vf9_blue.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vf9_red = new Vehicle();
        vf9_red.setManufacturer(edrive);
        vf9_red.setModelName("VF 9");
        vf9_red.setVersion("Standard");
        vf9_red.setColor(red);
        vf9_red.setBatteryCapacityKwh(42);
        vf9_red.setRangeKm(285);
        vf9_red.setMaxSpeedKmh(150);
        vf9_red.setChargingTimeHours(5.5f);
        vf9_red.setSeatingCapacity(5);
        vf9_red.setMotorPowerKw(110);
        vf9_red.setWeightKg(1610);
        vf9_red.setLengthMm(4300);
        vf9_red.setWidthMm(1793);
        vf9_red.setHeightMm(1613);
        vf9_red.setPriceRetail(new BigDecimal("3000"));
        vf9_red.setManufactureYear(2023);
        vf9_red.setImageUrl("https://vinfast-hcm.vn/wp-content/uploads/2022/12/4.png");
        vf9_red.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vf9_white = new Vehicle();
        vf9_white.setManufacturer(edrive);
        vf9_white.setModelName("VF 9");
        vf9_white.setVersion("Standard");
        vf9_white.setColor(white);
        vf9_white.setBatteryCapacityKwh(42);
        vf9_white.setRangeKm(285);
        vf9_white.setMaxSpeedKmh(150);
        vf9_white.setChargingTimeHours(5.5f);
        vf9_white.setSeatingCapacity(5);
        vf9_white.setMotorPowerKw(110);
        vf9_white.setWeightKg(1610);
        vf9_white.setLengthMm(4300);
        vf9_white.setWidthMm(1793);
        vf9_white.setHeightMm(1613);
        vf9_white.setPriceRetail(new BigDecimal("3000"));
        vf9_white.setManufactureYear(2023);
        vf9_white.setImageUrl("https://vinfastdienchau.com/wp-content/uploads/2013/08/VF-9-Plus_US-CA_21-inch_Brahminy-White_Mid.png");
        vf9_white.setStatus(VehicleStatus.AVAILABLE);

// ====== Khởi tạo Vehicles cho Tesla ======
        Vehicle vf3_yellow = new Vehicle();
        vf3_yellow.setManufacturer(edrive);
        vf3_yellow.setModelName("VF 3");
        vf3_yellow.setVersion("Long Range");
        vf3_yellow.setColor(yellow); // giữ “Xanh” nhưng có mã hex riêng
        vf3_yellow.setBatteryCapacityKwh(82);
        vf3_yellow.setRangeKm(602);
        vf3_yellow.setMaxSpeedKmh(233);
        vf3_yellow.setChargingTimeHours(8.0f);
        vf3_yellow.setSeatingCapacity(5);
        vf3_yellow.setMotorPowerKw(283);
        vf3_yellow.setWeightKg(1844);
        vf3_yellow.setLengthMm(4694);
        vf3_yellow.setWidthMm(1849);
        vf3_yellow.setHeightMm(1443);
        vf3_yellow.setPriceRetail(new BigDecimal("1500000000"));
        vf3_yellow.setManufactureYear(2024);
        vf3_yellow.setImageUrl("https://vinfastotominhdao.vn/wp-content/uploads/vf3-3034-5-scaled.jpg");
        vf3_yellow.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vf3_red = new Vehicle();
        vf3_red.setManufacturer(edrive);
        vf3_red.setModelName("VF 3");
        vf3_red.setVersion("Long Range");
        vf3_red.setColor(red); // giữ “Xanh” nhưng có mã hex riêng
        vf3_red.setBatteryCapacityKwh(82);
        vf3_red.setRangeKm(602);
        vf3_red.setMaxSpeedKmh(233);
        vf3_red.setChargingTimeHours(8.0f);
        vf3_red.setSeatingCapacity(5);
        vf3_red.setMotorPowerKw(283);
        vf3_red.setWeightKg(1844);
        vf3_red.setLengthMm(4694);
        vf3_red.setWidthMm(1849);
        vf3_red.setHeightMm(1443);
        vf3_red.setPriceRetail(new BigDecimal("1500000000"));
        vf3_red.setManufactureYear(2024);
        vf3_red.setImageUrl("https://vinfast-cars.vn/wp-content/uploads/2024/10/vinfast-vf3-do.png");
        vf3_red.setStatus(VehicleStatus.AVAILABLE);

// ====== Khởi tạo Vehicles cho BYD ======
        Vehicle vf5_red = new Vehicle();
        vf5_red.setManufacturer(edrive);
        vf5_red.setModelName("VF 5");
        vf5_red.setVersion("Long Range");
        vf5_red.setColor(red); // giữ “Xanh” nhưng có mã hex riêng
        vf5_red.setBatteryCapacityKwh(82);
        vf5_red.setRangeKm(602);
        vf5_red.setMaxSpeedKmh(233);
        vf5_red.setChargingTimeHours(8.0f);
        vf5_red.setSeatingCapacity(5);
        vf5_red.setMotorPowerKw(283);
        vf5_red.setWeightKg(1844);
        vf5_red.setLengthMm(4694);
        vf5_red.setWidthMm(1849);
        vf5_red.setHeightMm(1443);
        vf5_red.setPriceRetail(new BigDecimal("1500000000"));
        vf5_red.setManufactureYear(2024);
        vf5_red.setImageUrl("https://vinfasthadong.com.vn/wp-content/uploads/2023/10/vf5-2023.jpg");
        vf5_red.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vf5_grey = new Vehicle();
        vf5_grey.setManufacturer(edrive);
        vf5_grey.setModelName("VF 5");
        vf5_grey.setVersion("Long Range");
        vf5_grey.setColor(gray); // giữ “Xanh” nhưng có mã hex riêng
        vf5_grey.setBatteryCapacityKwh(82);
        vf5_grey.setRangeKm(602);
        vf5_grey.setMaxSpeedKmh(233);
        vf5_grey.setChargingTimeHours(8.0f);
        vf5_grey.setSeatingCapacity(5);
        vf5_grey.setMotorPowerKw(283);
        vf5_grey.setWeightKg(1844);
        vf5_grey.setLengthMm(4694);
        vf5_grey.setWidthMm(1849);
        vf5_grey.setHeightMm(1443);
        vf5_grey.setPriceRetail(new BigDecimal("1500000000"));
        vf5_grey.setManufactureYear(2024);
        vf5_grey.setImageUrl("https://vinfastgiare.vn/public/upload/images/vinfastgiare-9.jpg");
        vf5_grey.setStatus(VehicleStatus.AVAILABLE);


        Vehicle vf6_green = new Vehicle();
        vf6_green.setManufacturer(edrive);
        vf6_green.setModelName("VF 6");
        vf6_green.setVersion("Long Range");
        vf6_green.setColor(green); // giữ “Xanh” nhưng có mã hex riêng
        vf6_green.setBatteryCapacityKwh(82);
        vf6_green.setRangeKm(602);
        vf6_green.setMaxSpeedKmh(233);
        vf6_green.setChargingTimeHours(8.0f);
        vf6_green.setSeatingCapacity(5);
        vf6_green.setMotorPowerKw(283);
        vf6_green.setWeightKg(1844);
        vf6_green.setLengthMm(4694);
        vf6_green.setWidthMm(1849);
        vf6_green.setHeightMm(1443);
        vf6_green.setPriceRetail(new BigDecimal("1500000000"));
        vf6_green.setManufactureYear(2024);
        vf6_green.setImageUrl("https://vinfastcarhanoi.com/wp-content/uploads/2025/02/vinfast-vf6-3.png");
        vf6_green.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vf6_red = new Vehicle();
        vf6_red.setManufacturer(edrive);
        vf6_red.setModelName("VF 6");
        vf6_red.setVersion("Long Range");
        vf6_red.setColor(red); // giữ “Xanh” nhưng có mã hex riêng
        vf6_red.setBatteryCapacityKwh(82);
        vf6_red.setRangeKm(602);
        vf6_red.setMaxSpeedKmh(233);
        vf6_red.setChargingTimeHours(8.0f);
        vf6_red.setSeatingCapacity(5);
        vf6_red.setMotorPowerKw(283);
        vf6_red.setWeightKg(1844);
        vf6_red.setLengthMm(4694);
        vf6_red.setWidthMm(1849);
        vf6_red.setHeightMm(1443);
        vf6_red.setPriceRetail(new BigDecimal("1500000000"));
        vf6_red.setManufactureYear(2024);
        vf6_red.setImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRA__L11pM72Iq-5JNiaQ4VrzE4o4ox9MOxRA&s");
        vf6_red.setStatus(VehicleStatus.AVAILABLE);


        Vehicle vf7_white = new Vehicle();
        vf7_white.setManufacturer(edrive);
        vf7_white.setModelName("VF 7");
        vf7_white.setVersion("Long Range");
        vf7_white.setColor(white); // giữ “Xanh” nhưng có mã hex riêng
        vf7_white.setBatteryCapacityKwh(82);
        vf7_white.setRangeKm(602);
        vf7_white.setMaxSpeedKmh(233);
        vf7_white.setChargingTimeHours(8.0f);
        vf7_white.setSeatingCapacity(5);
        vf7_white.setMotorPowerKw(283);
        vf7_white.setWeightKg(1844);
        vf7_white.setLengthMm(4694);
        vf7_white.setWidthMm(1849);
        vf7_white.setHeightMm(1443);
        vf7_white.setPriceRetail(new BigDecimal("1500000000"));
        vf7_white.setManufactureYear(2024);
        vf7_white.setImageUrl("https://www.vinfastmiennam.vn/images/bf432-vf7-plus.png");
        vf7_white.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vf7_red = new Vehicle();
        vf7_red.setManufacturer(edrive);
        vf7_red.setModelName("VF 7");
        vf7_red.setVersion("Long Range");
        vf7_red.setColor(red); // giữ “Xanh” nhưng có mã hex riêng
        vf7_red.setBatteryCapacityKwh(82);
        vf7_red.setRangeKm(602);
        vf7_red.setMaxSpeedKmh(233);
        vf7_red.setChargingTimeHours(8.0f);
        vf7_red.setSeatingCapacity(5);
        vf7_red.setMotorPowerKw(283);
        vf7_red.setWeightKg(1844);
        vf7_red.setLengthMm(4694);
        vf7_red.setWidthMm(1849);
        vf7_red.setHeightMm(1443);
        vf7_red.setPriceRetail(new BigDecimal("1500000000"));
        vf7_red.setManufactureYear(2024);
        vf7_red.setImageUrl("https://img.tinxe.vn/crop/730x410/2024/01/01/0MQViQLm/vinfast-vf7-5-51ca.webp");
        vf7_red.setStatus(VehicleStatus.AVAILABLE);


        List<Vehicle> vehicles = vehicleRepository.saveAll(
                Arrays.asList(
                        vf8_silver, vf8_red,
                        vf9_white, vf9_blue, vf9_red,
                        vf3_yellow, vf3_red,
                        vf5_red, vf5_grey,
                        vf6_red, vf6_green,
                        vf7_white, vf7_red
                )
        );
        System.out.println("✅ Đã khởi tạo " + vehicles.size() + " vehicles");

        // Khởi tạo Manufacturer Inventories
        LocalDateTime now = LocalDateTime.now();

        int defaultQty = 30;
        Map<String, Integer> qtyByModel = new HashMap<>();
        qtyByModel.put("VF 3", 45);
        qtyByModel.put("VF 5", 60);
        qtyByModel.put("VF 6", 40);
        qtyByModel.put("VF 7", 35);
        qtyByModel.put("VF 8", 50);
        qtyByModel.put("VF 9", 30);

        List<ManufacturerInventory> inventories = new ArrayList<>();

        for (Vehicle v : vehicles) {
            int qty = qtyByModel.getOrDefault(v.getModelName(), defaultQty);

            ManufacturerInventory inv = ManufacturerInventory.builder()
                    .manufacturer(edrive)   // 1 kho: luôn cùng manufacturer
                    .vehicle(v)
                    .quantity(qty)
                    .lastUpdated(now)
                    .build();

            inventories.add(inv);
        }

        inventories = manufacturerInventoryRepository.saveAll(inventories);
        System.out.println("✅ Đã khởi tạo " + inventories.size() + " manufacturer inventories");
// =================== SEED PROMOTIONS ===================
        if (promotionRepository.count() == 0) {
            List<Promotion> promotions = new ArrayList<>();

            Promotion promo1 = new Promotion();
            promo1.setTitle("Khuyến mãi đầu năm - VinFast VF 8");
            promo1.setDescription("Giảm giá đặc biệt cho mẫu VF 8 trong tháng khai xuân! Nhận ngay 5% giảm giá khi đặt xe trước 30/3.");
            promo1.setDiscountType(DiscountType.PERCENTAGE);
            promo1.setDiscountValue(5.0);
            promo1.setStartDate(LocalDate.now().minusDays(10));
            promo1.setEndDate(LocalDate.now().plusDays(20));
            promo1.setApplicableTo(PromoTarget.CUSTOMER);
            promo1.setDealer(dealersInDb.get(0)); // Đại lý Hà Nội
            promo1.getVehicles().add(vehicles.get(0)); // VF8

            Promotion promo2 = new Promotion();
            promo2.setTitle("Siêu ưu đãi Tesla Model 3");
            promo2.setDescription("Tặng gói sạc nhanh và giảm 100 triệu cho khách hàng mua Tesla Model 3 trong tháng này.");
            promo2.setDiscountType(DiscountType.PERCENTAGE);
            promo2.setDiscountValue(100_000_000.0);
            promo2.setStartDate(LocalDate.now().minusDays(5));
            promo2.setEndDate(LocalDate.now().plusDays(25));
            promo2.setApplicableTo(PromoTarget.CUSTOMER);
            promo2.setDealer(dealersInDb.get(1)); // HCM
            promo2.getVehicles().add(vehicles.get(3)); // Model 3

            Promotion promo3 = new Promotion();
            promo3.setTitle("Chương trình tri ân đại lý");
            promo3.setDescription("Giảm 10% giá nhập xe VinFast cho tất cả đại lý trong hệ thống.");
            promo3.setDiscountType(DiscountType.PERCENTAGE);
            promo3.setDiscountValue(10.0);
            promo3.setStartDate(LocalDate.now());
            promo3.setEndDate(LocalDate.now().plusDays(45));
            promo3.setApplicableTo(PromoTarget.DEALER);
            promo3.setDealer(dealersInDb.get(2)); // Đà Nẵng
            promo3.getVehicles().add(vehicles.get(0));
            promo3.getVehicles().add(vehicles.get(1)); // VF8, vf9_blue

            Promotion promo4 = new Promotion();
            promo4.setTitle("Toàn quốc - Mua xe BYD nhận quà");
            promo4.setDescription("Áp dụng toàn quốc: Khách hàng mua xe BYD bất kỳ sẽ nhận phiếu quà tặng trị giá 20 triệu đồng.");
            promo4.setDiscountType(DiscountType.FIXED_AMOUNT);
            promo4.setDiscountValue(20_000_000.0);
            promo4.setStartDate(LocalDate.now().minusDays(15));
            promo4.setEndDate(LocalDate.now().plusDays(60));
            promo4.setApplicableTo(PromoTarget.ALL);
            promo4.setDealer(dealersInDb.get(4)); // Cần Thơ
            promo4.getVehicles().add(vehicles.get(5)); // Han
            promo4.getVehicles().add(vehicles.get(6)); // Atto 3

            promotions.addAll(List.of(promo1, promo2, promo3, promo4));
            promotionRepository.saveAll(promotions);
            System.out.println("✅ Đã khởi tạo " + promotions.size() + " promotions");
        }
        // =================== SEED CUSTOMERS ===================
        if (customerRepository.count() == 0) {
            List<Dealer> dealers = dealerRepository.findAll();
            List<Customer> customers = new ArrayList<>();

            if (!dealers.isEmpty()) {
                Dealer dealer1 = dealers.get(0);
                Dealer dealer2 = dealers.size() > 1 ? dealers.get(1) : dealer1;

                Customer c1 = new Customer();
                c1.setFullName("Nguyễn Văn Minh");
                c1.setDob(LocalDate.of(1992, 3, 15));
                c1.setGender("Nam");
                c1.setEmail("minh.nguyen@example.com");
                c1.setPhone("0905123456");
                c1.setAddress("12 Nguyễn Trãi, Hà Nội");
                c1.setIdCardNo("012345678912");
                c1.setDealer(dealer1);

                Customer c2 = new Customer();
                c2.setFullName("Trần Thị Hồng");
                c2.setDob(LocalDate.of(1996, 8, 25));
                c2.setGender("Nữ");
                c2.setEmail("hong.tran@example.com");
                c2.setPhone("0906234567");
                c2.setAddress("56 Lê Lợi, Quận 1, TP.HCM");
                c2.setIdCardNo("123456789123");
                c2.setDealer(dealer2);

                Customer c3 = new Customer();
                c3.setFullName("Lê Quốc Anh");
                c3.setDob(LocalDate.of(1988, 11, 5));
                c3.setGender("Nam");
                c3.setEmail("anh.le@example.com");
                c3.setPhone("0907345678");
                c3.setAddress("89 Nguyễn Văn Linh, Đà Nẵng");
                c3.setIdCardNo("234567891234");
                c3.setDealer(dealer1);

                customers.addAll(List.of(c1, c2, c3));
                customerRepository.saveAll(customers);

                System.out.println("✅ Đã khởi tạo " + customers.size() + " customers");
            }
        }
        // =================== SEED ORDER CUSTOMERS ===================
        if (orderCustomerRepository.count() == 0) {
            List<Customer> customers = customerRepository.findAll();
            List<Dealer> dealers = dealerRepository.findAll();

            if (customers.size() >= 3 && dealers.size() >= 3 && vehicles.size() >= 5) {
                Customer c1 = customers.get(0);
                Customer c2 = customers.get(1);
                Customer c3 = customers.get(2);


                Dealer d1 = dealers.get(0);
                Dealer d2 = dealers.get(1);
                Dealer d3 = dealers.get(2);


                Vehicle v1 = vehicles.get(0);
                Vehicle v2 = vehicles.get(1);
                Vehicle v3 = vehicles.get(2);
                Vehicle v4 = vehicles.get(3);
                Vehicle v5 = vehicles.get(4);
                Vehicle v6 = vehicles.size() > 5 ? vehicles.get(5) : v1;

                // 🟢 Các đơn hàng mẫu
                OrderCustomer o1 = new OrderCustomer();
                o1.setOrderCode("ORD-2025-001");
                o1.setCustomer(c1);
                o1.setDealer(d1);
                o1.setVehicle(v1);

                OrderCustomer o2 = new OrderCustomer();
                o2.setOrderCode("ORD-2025-002");
                o2.setCustomer(c2);
                o2.setDealer(d2);
                o2.setVehicle(v2);

                OrderCustomer o3 = new OrderCustomer();
                o3.setOrderCode("ORD-2025-003");
                o3.setCustomer(c3);
                o3.setDealer(d3);
                o3.setVehicle(v3);

                OrderCustomer o4 = new OrderCustomer();
                o4.setOrderCode("ORD-2025-004");
                o4.setCustomer(c1);
                o4.setDealer(d2);
                o4.setVehicle(v4);

                OrderCustomer o5 = new OrderCustomer();
                o5.setOrderCode("ORD-2025-005");
                o5.setCustomer(c2);
                o5.setDealer(d2);
                o5.setVehicle(v5);

                OrderCustomer o6 = new OrderCustomer();
                o6.setOrderCode("ORD-2025-006");
                o6.setCustomer(c1);
                o6.setDealer(d1);
                o6.setVehicle(v6);

                OrderCustomer o7 = new OrderCustomer();
                o7.setOrderCode("ORD-2025-007");
                o7.setCustomer(c2);
                o7.setDealer(d3);
                o7.setVehicle(v2);

                // Lưu trước order để có ID cho khóa ngoại
                orderCustomerRepository.saveAll(List.of(o1, o2, o3, o4, o5, o6, o7));

                // 🟡 Trạng thái cho từng order
                StatusOrderCustomer s1 = new StatusOrderCustomer();
                s1.setStatus("Đã phân bổ xe");
                s1.setDeliveryDate("2025-11-10");
                s1.setDeliveryLocation("TP. Hồ Chí Minh");
                s1.setOrderCustomer(o1);

                StatusOrderCustomer s2 = new StatusOrderCustomer();
                s2.setStatus("Chờ xử lý");
                s2.setDeliveryDate("Chưa hẹn");
                s2.setDeliveryLocation("Hà Nội");
                s2.setOrderCustomer(o2);

                StatusOrderCustomer s3 = new StatusOrderCustomer();
                s3.setStatus("Đang vận chuyển");
                s3.setDeliveryDate("2025-11-15");
                s3.setDeliveryLocation("Hà Nội");
                s3.setOrderCustomer(o3);

                StatusOrderCustomer s4 = new StatusOrderCustomer();
                s4.setStatus("Đã giao hàng");
                s4.setDeliveryDate("2025-10-28");
                s4.setDeliveryLocation("Đà Nẵng");
                s4.setOrderCustomer(o4);

                StatusOrderCustomer s5 = new StatusOrderCustomer();
                s5.setStatus("Hủy đơn");
                s5.setDeliveryDate("Không có");
                s5.setDeliveryLocation("Khánh Hòa");
                s5.setOrderCustomer(o5);

                StatusOrderCustomer s6 = new StatusOrderCustomer();
                s6.setStatus("Đang chuẩn bị xe");
                s6.setDeliveryDate("2025-11-12");
                s6.setDeliveryLocation("TP.Hồ Chí Minh");
                s6.setOrderCustomer(o6);

                StatusOrderCustomer s7 = new StatusOrderCustomer();
                s7.setStatus("Đang chờ xác nhận thanh toán");
                s7.setDeliveryDate("Chưa xác định");
                s7.setDeliveryLocation("Cam Ranh");
                s7.setOrderCustomer(o7);

                // Gắn 2 chiều
                o1.setStatusOrderCustomer(s1);
                o2.setStatusOrderCustomer(s2);
                o3.setStatusOrderCustomer(s3);
                o4.setStatusOrderCustomer(s4);
                o5.setStatusOrderCustomer(s5);
                o6.setStatusOrderCustomer(s6);
                o7.setStatusOrderCustomer(s7);

                // Lưu
                statusOrderCustomerRepository.saveAll(List.of(s1, s2, s3, s4, s5, s6, s7));
                orderCustomerRepository.saveAll(List.of(o1, o2, o3, o4, o5, o6, o7));

                System.out.println("✅ Đã khởi tạo 7 đơn hàng mẫu (OrderCustomer + StatusOrderCustomer)");
            }
        }

// =================== SEED TEST DRIVES ===================
        if (testDriveRepository.count() == 0) {
            List<Dealer> dealers = dealerRepository.findAll();
            List<Customer> customers = customerRepository.findAll();

            if (!dealers.isEmpty() && !vehicles.isEmpty() && !customers.isEmpty()) {
                Dealer dealer1 = dealers.get(0);
                Dealer dealer2 = dealers.size() > 1 ? dealers.get(1) : dealer1;

                Customer customer1 = customers.get(0);
                Customer customer2 = customers.size() > 1 ? customers.get(1) : customer1;

                Vehicle vehicle1 = vehicles.get(0);
                Vehicle vehicle2 = vehicles.size() > 1 ? vehicles.get(1) : vehicle1;

                // TestDrive 1 — Pending
                TestDrive td1 = new TestDrive();
                td1.setCustomer(customer1);
                td1.setDealer(dealer1);
                td1.setVehicle(vehicle1);
                td1.setScheduleDatetime(LocalDateTime.now().plusDays(2));
                td1.setStatus(com.swp391.edrive.enums.TestDriveStatus.PENDING);

                // TestDrive 2 — Completed
                TestDrive td2 = new TestDrive();
                td2.setCustomer(customer2);
                td2.setDealer(dealer1);
                td2.setVehicle(vehicle2);
                td2.setScheduleDatetime(LocalDateTime.now().minusDays(1));
                td2.setCompletedAt(LocalDateTime.now().minusHours(3));
                td2.setStatus(com.swp391.edrive.enums.TestDriveStatus.COMPLETED);

                // TestDrive 3 — Cancelled
                TestDrive td3 = new TestDrive();
                td3.setCustomer(customer1);
                td3.setDealer(dealer2);
                td3.setVehicle(vehicle2);
                td3.setScheduleDatetime(LocalDateTime.now().plusDays(5));
                td3.setStatus(com.swp391.edrive.enums.TestDriveStatus.CANCELLED);
                td3.setCancelReason("Khách bận công tác, hủy lịch.");

                testDriveRepository.saveAll(List.of(td1, td2, td3));
                System.out.println("✅ Đã khởi tạo 3 test drives");
            }
        }

        // =================== SEED FEEDBACK (đăng ký/lái thử) ===================
        if (feedbackRepository.count() == 0) {
            List<Customer> customers = customerRepository.findAll();
            List<Dealer> dealers = dealerRepository.findAll();

            if (!customers.isEmpty() && !dealers.isEmpty()) {
                // Feedback 1: Khách 1 – chấm điểm quy trình ĐĂNG KÝ lái thử
                Feedback f1 = new Feedback();
                f1.setCustomer(customers.get(0));
                f1.setDealer(dealers.get(0));
                f1.setRating(4); // CSAT 1-5
                f1.setContent("Đăng ký lái thử khá nhanh, nhân viên gọi xác nhận trong 15 phút.");
                f1.setCreatedAt(LocalDateTime.now().minusDays(1));

                // Feedback 2: Khách 2 – chấm điểm sau LÁI THỬ (đã hoàn tất)
                Feedback f2 = new Feedback();
                f2.setCustomer(customers.size() > 1 ? customers.get(1) : customers.get(0));
                f2.setDealer(dealers.get(0));
                f2.setRating(5);
                f2.setContent("Trải nghiệm lái thử tuyệt vời, xe êm và tăng tốc tốt. Nhân viên hướng dẫn kỹ.");
                f2.setCreatedAt(LocalDateTime.now().minusHours(6));

                // Feedback 3: Khách 3 – đăng ký nhưng HUỶ lịch (phản hồi lý do)
                Feedback f3 = new Feedback();
                f3.setCustomer(customers.size() > 2 ? customers.get(2) : customers.get(0));
                f3.setDealer(dealers.size() > 1 ? dealers.get(1) : dealers.get(0));
                f3.setRating(3);
                f3.setContent("Tôi bận đột xuất nên huỷ lịch. Quy trình huỷ đơn giản, mong sắp xếp lại cuối tuần.");
                f3.setCreatedAt(LocalDateTime.now().minusDays(2));

                feedbackRepository.saveAll(List.of(f1, f2, f3));
                System.out.println("✅ Đã khởi tạo 3 feedbacks cho khách hàng (đăng ký/lái thử)");
            }
        }


        System.out.println("🎉 Hoàn thành khởi tạo dữ liệu!");
    }

    private Dealer makeDealer(String name, String street, String ward, String district, String city, String contact, String phone) {
        Dealer d = new Dealer();
        d.setDealerName(name);
        d.setHouseNumberAndStreet(street);
        d.setWardOrCommune(ward);
        d.setDistrict(district);
        d.setProvinceOrCity(city);
        d.setContactPerson(contact);
        d.setPhone(phone);
        return d;
    }

    private Color upsertColor(String colorName, String hexCode) {
        return colorRepository.findByColorNameIgnoreCase(colorName)
                .map(existing -> {
                    if (hexCode != null && (existing.getHexCode() == null || !existing.getHexCode().equalsIgnoreCase(hexCode))) {
                        existing.setHexCode(hexCode);
                        return colorRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    Color c = new Color();
                    c.setColorName(colorName);
                    c.setHexCode(hexCode);
                    return colorRepository.save(c);
                });
    }

}