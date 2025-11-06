package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.ManufacturerInventoryRequest;
import com.swp391.edrive.dto.request.ManufacturerInventoryUpdateRequest;
import com.swp391.edrive.dto.response.DealerQuantityResponse;
import com.swp391.edrive.dto.response.ManufacturerInventoryResponse;
import com.swp391.edrive.dto.response.ManufacturerInventorySummaryResponse;
import com.swp391.edrive.dto.response.VehicleInventoryResponse;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.repository.*;
import com.swp391.edrive.service.ManufacturerInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ManufacturerInventoryServiceImpl implements ManufacturerInventoryService {

    private final ManufacturerInventoryRepository manufacturerInventoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final VehicleRepository vehicleRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<ManufacturerInventoryResponse> getAll() {
        return manufacturerInventoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ManufacturerInventoryResponse getById(Long id) {
        ManufacturerInventory inv = manufacturerInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        return toResponse(inv);
    }

    @Override
    public ManufacturerInventoryResponse create(ManufacturerInventoryRequest request) {
        Manufacturer manufacturer = manufacturerRepository.findByManufacturerName("EDrive")
                .orElseThrow(() -> new RuntimeException("Manufacturer 'eDrive' not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        boolean exists = manufacturerInventoryRepository.existsByVehicle_VehicleId(request.getVehicleId());
        if (exists) {
            throw new RuntimeException("Xe này đã tồn tại trong kho tổng, không thể thêm trùng!");
        }

        ManufacturerInventory inv = ManufacturerInventory.builder()
                .manufacturer(manufacturer)
                .vehicle(vehicle)
                .quantity(request.getQuantity())
                .lastUpdated(LocalDateTime.now())
                .build();

        manufacturerInventoryRepository.save(inv);
        return toResponse(inv);
    }



    @Override
    public ManufacturerInventoryResponse update(Long vehicleId, ManufacturerInventoryUpdateRequest request) {
        ManufacturerInventory inv = manufacturerInventoryRepository.findByVehicle_VehicleId(vehicleId)
                .orElseThrow(() -> new RuntimeException("Manufacturer inventory not found for vehicleId: " + vehicleId));

        inv.setQuantity(request.getQuantity());
        inv.setLastUpdated(LocalDateTime.now());

        manufacturerInventoryRepository.save(inv);

        return toResponse(inv);
    }



    @Override
    public void delete(Long id) {
        if (!manufacturerInventoryRepository.existsById(id)) {
            throw new RuntimeException("Inventory not found");
        }
        manufacturerInventoryRepository.deleteById(id);
    }
    @Override
    public List<ManufacturerInventorySummaryResponse> getGroupedByManufacturer() {
        List<ManufacturerInventory> inventories = manufacturerInventoryRepository.findAll();
        List<OrderItem> orderItems = orderItemRepository.findAll();

        Map<Manufacturer, List<ManufacturerInventory>> groupedByManufacturer = inventories.stream()
                .collect(Collectors.groupingBy(ManufacturerInventory::getManufacturer));

        return groupedByManufacturer.entrySet().stream().map(entry -> {
            Manufacturer manufacturer = entry.getKey();
            List<ManufacturerInventory> items = entry.getValue();

            int totalQuantity = items.stream()
                    .mapToInt(ManufacturerInventory::getQuantity)
                    .sum();

            Map<String, List<ManufacturerInventory>> groupedByModel = items.stream()
                    .collect(Collectors.groupingBy(inv -> inv.getVehicle().getModelName()));

            List<VehicleInventoryResponse> vehicles = groupedByModel.entrySet().stream()
                    .flatMap(modelEntry -> {
                        String vehicleName = modelEntry.getKey();
                        List<ManufacturerInventory> sameVehicles = modelEntry.getValue();

                        // 🔹 Lấy OrderItem liên quan đến các vehicle cùng modelName
                        List<OrderItem> relatedOrderItems = orderItems.stream()
                                .filter(oi -> oi.getVehicle().getModelName().equals(vehicleName))
                                .toList();

                        // Tính toán tổng xuất kho và đang giao
                        Map<Long, Integer> exportedMap = relatedOrderItems.stream()
                                .filter(oi -> oi.getOrder().getStatus() == OrderStatus.DELIVERED)
                                .collect(Collectors.groupingBy(
                                        oi -> oi.getVehicle().getVehicleId(),
                                        Collectors.summingInt(OrderItem::getQuantity)
                                ));

                        Map<Long, Integer> inDeliveryMap = relatedOrderItems.stream()
                                .filter(oi -> oi.getOrder().getStatus() == OrderStatus.PROCESSING)
                                .collect(Collectors.groupingBy(
                                        oi -> oi.getVehicle().getVehicleId(),
                                        Collectors.summingInt(OrderItem::getQuantity)
                                ));

                        return sameVehicles.stream()
                                .sorted((a, b) -> a.getManufacturerInventoryId().compareTo(b.getManufacturerInventoryId()))
                                .map(inv -> {
                                    Vehicle vehicle = inv.getVehicle();
                                    Long vehicleId = vehicle.getVehicleId();

                                    int exportedQuantity = exportedMap.getOrDefault(vehicleId, 0);
                                    int inDeliveryQuantity = inDeliveryMap.getOrDefault(vehicleId, 0);

                                    // Gom nhóm theo đại lý
                                    Map<String, Integer> dealerMap = relatedOrderItems.stream()
                                            .filter(oi -> oi.getVehicle().getVehicleId().equals(vehicleId))
                                            .collect(Collectors.groupingBy(
                                                    oi -> oi.getOrder().getDealer().getDealerName(),
                                                    Collectors.summingInt(OrderItem::getQuantity)
                                            ));

                                    List<DealerQuantityResponse> dealers = dealerMap.entrySet().stream()
                                            .map(e -> DealerQuantityResponse.builder()
                                                    .dealerName(e.getKey())
                                                    .quantity(e.getValue())
                                                    .build())
                                            .toList();

                                    return VehicleInventoryResponse.builder()
                                            .manufacturerInventoryId(inv.getManufacturerInventoryId())
                                            .vehicleId(vehicleId)
                                            .vehicleName(vehicleName)
                                            .version(vehicle.getVersion())
                                            .color(vehicle.getColor() != null ? vehicle.getColor().getColorName() : null)
                                            .quantity(inv.getQuantity())
                                            .exportedQuantity(exportedQuantity)
                                            .inDeliveryQuantity(inDeliveryQuantity)
                                            .dealers(dealers)
                                            .build();
                                });
                    })
                    .toList();

            return ManufacturerInventorySummaryResponse.builder()
                    .manufacturerName(manufacturer.getManufacturerName())
                    .totalQuantity(totalQuantity)
                    .vehicles(vehicles)
                    .build();
        }).toList();
    }



    private ManufacturerInventoryResponse toResponse(ManufacturerInventory inv) {
        return ManufacturerInventoryResponse.builder()
                .manufacturerInventoryId(inv.getManufacturerInventoryId())
                .manufacturerName(inv.getManufacturer().getManufacturerName())
                .vehicleId(inv.getVehicle().getVehicleId())
                .vehicleName(inv.getVehicle().getModelName())
                .quantity(inv.getQuantity())
                .lastUpdated(inv.getLastUpdated())
                .build();
    }
}
