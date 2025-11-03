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
        Role adminRole = roleRepository.findById("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").description("System Administrator").build()));
        Role dealerRole = roleRepository.findById("DEALER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("DEALER").description("Dealer user").build()));

        // 1.2 Admin user (nếu chưa có)
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123")) // đổi sau khi đăng nhập
                    .fullName("System Administrator")
                    .email("admin@edrive.local")
                    .phone("0000000000")
                    .isVerify(true)
                    .build();
            // đảm bảo roles không null khi dùng builder
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);
            userRepository.save(admin);
            System.out.println("✅ Seeded admin: admin / admin123");
        }

        // 1.3 Dealers & Dealer users
        // Nếu DB chưa có Dealer nào, tạo 10 Dealer mẫu
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

        // Tạo tài khoản dealer1..dealerN, gắn từng dealer
        int dealerUsersToCreate = Math.min(10, Math.max(1, dealersInDb.size()));
        for (int i = 1; i <= dealerUsersToCreate; i++) {
            String username = "dealer" + i;
            if (!userRepository.existsByUsername(username)) {
                Dealer boundDealer = dealersInDb.get((i - 1) % dealersInDb.size());
                User dealerUser = User.builder()
                        .username(username)
                        .password(passwordEncoder.encode("dealer123")) // đổi sau khi đăng nhập
                        .fullName(boundDealer.getDealerName() + " User")
                        .email(username + "@edrive.local")
                        .phone(boundDealer.getPhone() != null ? boundDealer.getPhone() : ("09" + String.format("%08d", i)))
                        .dealer(boundDealer)
                        .isVerify(true)
                        .build();
                Set<Role> roles = new HashSet<>();
                roles.add(dealerRole);
                dealerUser.setRoles(roles);
                userRepository.save(dealerUser);
                System.out.println("✅ Seeded dealer user: " + username + " / dealer213)");
            }
        }
        if (manufacturerInventoryRepository.count() > 0) {
            return;
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

// ====== Khởi tạo Vehicles cho VinFast ======
        Vehicle vf8 = new Vehicle();
        vf8.setManufacturer(edrive);
        vf8.setModelName("VF 8");
        vf8.setVersion("Eco");
        vf8.setColor(red); // <— dùng entity Color
        vf8.setBatteryCapacityKwh(87);
        vf8.setRangeKm(420);
        vf8.setMaxSpeedKmh(160);
        vf8.setChargingTimeHours(7.5f);
        vf8.setSeatingCapacity(5);
        vf8.setMotorPowerKw(260);
        vf8.setWeightKg(2050);
        vf8.setLengthMm(4750);
        vf8.setWidthMm(1934);
        vf8.setHeightMm(1667);
        vf8.setPriceRetail(new BigDecimal("1000"));
        vf8.setImageUrl("https://vinfastquangninh.com.vn/wp-content/uploads/2023/07/tai-xuong-8-min.png");
        vf8.setManufactureYear(2024);
        vf8.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vf9 = new Vehicle();
        vf9.setManufacturer(edrive);
        vf9.setModelName("VF 9");
        vf9.setVersion("Plus");
        vf9.setColor(black);
        vf9.setBatteryCapacityKwh(123);
        vf9.setRangeKm(594);
        vf9.setMaxSpeedKmh(200);
        vf9.setChargingTimeHours(9.0f);
        vf9.setSeatingCapacity(7);
        vf9.setMotorPowerKw(300);
        vf9.setWeightKg(2400);
        vf9.setLengthMm(5123);
        vf9.setWidthMm(1976);
        vf9.setHeightMm(1750);
        vf9.setPriceRetail(new BigDecimal("2000"));
        vf9.setManufactureYear(2024);
        vf9.setImageUrl("https://vinfasthadong.com.vn/wp-content/uploads/2023/10/VinFast-VF3-mau-den-scaled-1.jpg");
        vf9.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vfe34 = new Vehicle();
        vfe34.setManufacturer(edrive);
        vfe34.setModelName("VF e34");
        vfe34.setVersion("Standard");
        vfe34.setColor(white);
        vfe34.setBatteryCapacityKwh(42);
        vfe34.setRangeKm(285);
        vfe34.setMaxSpeedKmh(150);
        vfe34.setChargingTimeHours(5.5f);
        vfe34.setSeatingCapacity(5);
        vfe34.setMotorPowerKw(110);
        vfe34.setWeightKg(1610);
        vfe34.setLengthMm(4300);
        vfe34.setWidthMm(1793);
        vfe34.setHeightMm(1613);
        vfe34.setPriceRetail(new BigDecimal("3000"));
        vfe34.setManufactureYear(2023);
        vfe34.setImageUrl("https://vinfasthadong.com.vn/wp-content/uploads/2023/10/VinFast-VF3-mau-trang-2-scaled-1.jpg");
        vfe34.setStatus(VehicleStatus.AVAILABLE);

// ====== Khởi tạo Vehicles cho Tesla ======
        Vehicle model3 = new Vehicle();
        model3.setManufacturer(edrive);
        model3.setModelName("Model 3");
        model3.setVersion("Long Range");
        model3.setColor(cyan); // giữ “Xanh” nhưng có mã hex riêng
        model3.setBatteryCapacityKwh(82);
        model3.setRangeKm(602);
        model3.setMaxSpeedKmh(233);
        model3.setChargingTimeHours(8.0f);
        model3.setSeatingCapacity(5);
        model3.setMotorPowerKw(283);
        model3.setWeightKg(1844);
        model3.setLengthMm(4694);
        model3.setWidthMm(1849);
        model3.setHeightMm(1443);
        model3.setPriceRetail(new BigDecimal("1500000000"));
        model3.setManufactureYear(2024);
        model3.setImageUrl("https://vinfasthadong.com.vn/wp-content/uploads/2023/10/VinFast-VF3-mau-xanh-lo-Cyan-scaled-1.jpg");
        model3.setStatus(VehicleStatus.AVAILABLE);

        Vehicle modelY = new Vehicle();
        modelY.setManufacturer(edrive);
        modelY.setModelName("Model Y");
        modelY.setVersion("Performance");
        modelY.setColor(silver);
        modelY.setBatteryCapacityKwh(75);
        modelY.setRangeKm(514);
        modelY.setMaxSpeedKmh(250);
        modelY.setChargingTimeHours(7.5f);
        modelY.setSeatingCapacity(7);
        modelY.setMotorPowerKw(340);
        modelY.setWeightKg(2003);
        modelY.setLengthMm(4751);
        modelY.setWidthMm(1921);
        modelY.setHeightMm(1624);
        modelY.setPriceRetail(new BigDecimal("1800000000"));
        modelY.setManufactureYear(2024);
        modelY.setImageUrl("https://wuling-ev.vn/SEO/%C3%B4%20t%C3%B4%20%C4%91i%E1%BB%87n%20c%C5%A9%20gi%C3%A1%20r%E1%BA%BB/1834/image-thumb__1834___auto_697d1e38f8077a664d670954e166f84b/o-to-dien-cu-gia-re%20%281%29.70974c2f.jpg");
        modelY.setStatus(VehicleStatus.AVAILABLE);

// ====== Khởi tạo Vehicles cho BYD ======
        Vehicle han = new Vehicle();
        han.setManufacturer(edrive);
        han.setModelName("Han EV");
        han.setVersion("Premium");
        han.setColor(gray);
        han.setBatteryCapacityKwh(85);
        han.setRangeKm(605);
        han.setMaxSpeedKmh(185);
        han.setChargingTimeHours(8.5f);
        han.setSeatingCapacity(5);
        han.setMotorPowerKw(380);
        han.setWeightKg(2020);
        han.setLengthMm(4980);
        han.setWidthMm(1910);
        han.setHeightMm(1495);
        han.setPriceRetail(new BigDecimal("1400000000"));
        han.setManufactureYear(2024);
        han.setImageUrl("https://vinfasthadong.com.vn/wp-content/uploads/2023/10/VinFast-VF3-mau-bac-1-scaled-1.jpg");
        han.setStatus(VehicleStatus.AVAILABLE);

        Vehicle atto3 = new Vehicle();
        atto3.setManufacturer(edrive);
        atto3.setModelName("Atto 3");
        atto3.setVersion("Extended");
        atto3.setColor(blue);
        atto3.setBatteryCapacityKwh(60);
        atto3.setRangeKm(480);
        atto3.setMaxSpeedKmh(160);
        atto3.setChargingTimeHours(6.5f);
        atto3.setSeatingCapacity(5);
        atto3.setMotorPowerKw(150);
        atto3.setWeightKg(1750);
        atto3.setLengthMm(4455);
        atto3.setWidthMm(1875);
        atto3.setHeightMm(1615);
        atto3.setPriceRetail(new BigDecimal("850000000"));
        atto3.setManufactureYear(2024);
        atto3.setImageUrl("https://giaxevinfast.net/wp-content/uploads/2024/04/VinFast-VF-3-mau-Xanh-duong-dam-.png");
        atto3.setStatus(VehicleStatus.AVAILABLE);

        List<Vehicle> vehicles = vehicleRepository.saveAll(
                Arrays.asList(vf8, vf9, vfe34, model3, modelY, han, atto3)
        );
        System.out.println("✅ Đã khởi tạo " + vehicles.size() + " vehicles");

        // Khởi tạo Manufacturer Inventories
        LocalDateTime now = LocalDateTime.now();

        ManufacturerInventory inv1 = ManufacturerInventory.builder()
                .manufacturer(edrive)
                .vehicle(vf8)
                .quantity(50)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv2 = ManufacturerInventory.builder()
                .manufacturer(edrive)
                .vehicle(vf9)
                .quantity(30)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv3 = ManufacturerInventory.builder()
                .manufacturer(edrive)
                .vehicle(vfe34)
                .quantity(75)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv4 = ManufacturerInventory.builder()
                .manufacturer(edrive)
                .vehicle(model3)
                .quantity(40)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv5 = ManufacturerInventory.builder()
                .manufacturer(edrive)
                .vehicle(modelY)
                .quantity(35)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv6 = ManufacturerInventory.builder()
                .manufacturer(edrive)
                .vehicle(han)
                .quantity(45)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv7 = ManufacturerInventory.builder()
                .manufacturer(edrive)
                .vehicle(atto3)
                .quantity(60)
                .lastUpdated(now)
                .build();

        List<ManufacturerInventory> inventories = manufacturerInventoryRepository.saveAll(
                Arrays.asList(inv1, inv2, inv3, inv4, inv5, inv6, inv7)
        );
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
            promo3.getVehicles().add(vehicles.get(1)); // VF8, VF9

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