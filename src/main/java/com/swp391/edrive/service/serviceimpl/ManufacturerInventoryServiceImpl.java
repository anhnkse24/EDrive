package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.ManufacturerInventoryRequest;
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
        // 🟢 Tự động lấy manufacturer eDrive
        Manufacturer manufacturer = manufacturerRepository.findByManufacturerName("EDrive")
                .orElseThrow(() -> new RuntimeException("Manufacturer 'eDrive' not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

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
    public ManufacturerInventoryResponse update(Long id, ManufacturerInventoryRequest request) {
        ManufacturerInventory inv = manufacturerInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        // 🟢 Tự động set manufacturer = eDrive
        Manufacturer manufacturer = manufacturerRepository.findByManufacturerName("EDrive")
                .orElseThrow(() -> new RuntimeException("Manufacturer 'eDrive' not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        inv.setManufacturer(manufacturer);
        inv.setVehicle(vehicle);
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
    public List<ManufacturerInventorySummaryResponse> getGroupedByManufacturer() {
        List<ManufacturerInventory> inventories = manufacturerInventoryRepository.findAll();
        List<OrderItem> orderItems = orderItemRepository.findAll();

        // Nhóm theo manufacturer
        Map<Manufacturer, List<ManufacturerInventory>> grouped = inventories.stream()
                .collect(Collectors.groupingBy(ManufacturerInventory::getManufacturer));

        return grouped.entrySet().stream().map(entry -> {
            Manufacturer manufacturer = entry.getKey();
            List<ManufacturerInventory> items = entry.getValue();

            int totalQuantity = items.stream()
                    .mapToInt(ManufacturerInventory::getQuantity)
                    .sum();

            // Tạo danh sách VehicleInventoryResponse
            List<VehicleInventoryResponse> vehicles = items.stream().map(inv -> {
                Long vehicleId = inv.getVehicle().getVehicleId();

                // Lấy các OrderItem có liên quan đến xe này
                List<OrderItem> relatedOrderItems = orderItems.stream()
                        .filter(oi -> oi.getVehicle().getVehicleId().equals(vehicleId))
                        .toList();

                // Đếm số lượng đã giao
                int exportedQuantity = relatedOrderItems.stream()
                        .filter(oi -> oi.getOrder().getStatus() == OrderStatus.DELIVERED)
                        .mapToInt(OrderItem::getQuantity)
                        .sum();

                // Đếm số lượng đang điều phối
                int inDeliveryQuantity = relatedOrderItems.stream()
                        .filter(oi -> oi.getOrder().getStatus() == OrderStatus.PROCESSING)
                        .mapToInt(OrderItem::getQuantity)
                        .sum();

                // Gom nhóm theo đại lý
                Map<String, Integer> dealerMap = relatedOrderItems.stream()
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
                        .vehicleName(inv.getVehicle().getModelName())
                        .quantity(inv.getQuantity())
                        .exportedQuantity(exportedQuantity)
                        .inDeliveryQuantity(inDeliveryQuantity)
                        .dealers(dealers)
                        .build();
            }).toList();

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
