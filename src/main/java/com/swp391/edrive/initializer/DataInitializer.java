package com.swp391.edrive.initializer;

import com.swp391.edrive.entity.Manufacturer;
import com.swp391.edrive.entity.ManufacturerInventory;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.repository.ManufacturerInventoryRepository;
import com.swp391.edrive.repository.ManufacturerRepository;
import com.swp391.edrive.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ManufacturerInventoryRepository manufacturerInventoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (manufacturerInventoryRepository.count() > 0) {
            return;
        }

        // Khởi tạo Manufacturers
        Manufacturer vinfast = new Manufacturer();
        vinfast.setManufacturerName("VinFast");
        vinfast.setAddress("Khu Công nghệ cao Hòa Lạc, Huyện Thạch Thất, Hà Nội");
        vinfast.setContactPerson("Nguyễn Văn A");
        vinfast.setPhone("0243-123-4567");

        Manufacturer tesla = new Manufacturer();
        tesla.setManufacturerName("Tesla");
        tesla.setAddress("3500 Deer Creek Road, Palo Alto, CA 94304, USA");
        tesla.setContactPerson("John Smith");
        tesla.setPhone("+1-650-681-5000");

        Manufacturer byd = new Manufacturer();
        byd.setManufacturerName("BYD");
        byd.setAddress("No. 3009 BYD Road, Pingshan, Shenzhen, China");
        byd.setContactPerson("Li Wei");
        byd.setPhone("+86-755-8988-8888");

        List<Manufacturer> manufacturers = manufacturerRepository.saveAll(Arrays.asList(vinfast, tesla, byd));
        System.out.println("✅ Đã khởi tạo " + manufacturers.size() + " manufacturers");

        // Khởi tạo Vehicles cho VinFast
        Vehicle vf8 = new Vehicle();
        vf8.setManufacturer(vinfast);
        vf8.setModelName("VF 8");
        vf8.setVersion("Eco");
        vf8.setColor("Đỏ");
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
        vf8.setManufactureYear(2024);
        vf8.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vf9 = new Vehicle();
        vf9.setManufacturer(vinfast);
        vf9.setModelName("VF 9");
        vf9.setVersion("Plus");
        vf9.setColor("Đen");
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
        vf9.setStatus(VehicleStatus.AVAILABLE);

        Vehicle vfe34 = new Vehicle();
        vfe34.setManufacturer(vinfast);
        vfe34.setModelName("VF e34");
        vfe34.setVersion("Standard");
        vfe34.setColor("Trắng");
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
        vfe34.setStatus(VehicleStatus.AVAILABLE);

        // Khởi tạo Vehicles cho Tesla
        Vehicle model3 = new Vehicle();
        model3.setManufacturer(tesla);
        model3.setModelName("Model 3");
        model3.setVersion("Long Range");
        model3.setColor("Xanh");
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
        model3.setStatus(VehicleStatus.AVAILABLE);

        Vehicle modelY = new Vehicle();
        modelY.setManufacturer(tesla);
        modelY.setModelName("Model Y");
        modelY.setVersion("Performance");
        modelY.setColor("Bạc");
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
        modelY.setStatus(VehicleStatus.AVAILABLE);

        // Khởi tạo Vehicles cho BYD
        Vehicle han = new Vehicle();
        han.setManufacturer(byd);
        han.setModelName("Han EV");
        han.setVersion("Premium");
        han.setColor("Xám");
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
        han.setStatus(VehicleStatus.AVAILABLE);

        Vehicle atto3 = new Vehicle();
        atto3.setManufacturer(byd);
        atto3.setModelName("Atto 3");
        atto3.setVersion("Extended");
        atto3.setColor("Xanh Lam");
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
        atto3.setStatus(VehicleStatus.AVAILABLE);

        List<Vehicle> vehicles = vehicleRepository.saveAll(
                Arrays.asList(vf8, vf9, vfe34, model3, modelY, han, atto3)
        );
        System.out.println("✅ Đã khởi tạo " + vehicles.size() + " vehicles");

        // Khởi tạo Manufacturer Inventories
        LocalDateTime now = LocalDateTime.now();

        ManufacturerInventory inv1 = ManufacturerInventory.builder()
                .manufacturer(vinfast)
                .vehicle(vf8)
                .quantity(50)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv2 = ManufacturerInventory.builder()
                .manufacturer(vinfast)
                .vehicle(vf9)
                .quantity(30)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv3 = ManufacturerInventory.builder()
                .manufacturer(vinfast)
                .vehicle(vfe34)
                .quantity(75)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv4 = ManufacturerInventory.builder()
                .manufacturer(tesla)
                .vehicle(model3)
                .quantity(40)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv5 = ManufacturerInventory.builder()
                .manufacturer(tesla)
                .vehicle(modelY)
                .quantity(35)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv6 = ManufacturerInventory.builder()
                .manufacturer(byd)
                .vehicle(han)
                .quantity(45)
                .lastUpdated(now)
                .build();

        ManufacturerInventory inv7 = ManufacturerInventory.builder()
                .manufacturer(byd)
                .vehicle(atto3)
                .quantity(60)
                .lastUpdated(now)
                .build();

        List<ManufacturerInventory> inventories = manufacturerInventoryRepository.saveAll(
                Arrays.asList(inv1, inv2, inv3, inv4, inv5, inv6, inv7)
        );
        System.out.println("✅ Đã khởi tạo " + inventories.size() + " manufacturer inventories");
        System.out.println("🎉 Hoàn thành khởi tạo dữ liệu!");
    }

}