package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.ManufacturerInventoryRequest;
import com.swp391.edrive.dto.response.ManufacturerInventoryResponse;
import com.swp391.edrive.dto.response.ManufacturerInventorySummaryResponse;
import com.swp391.edrive.dto.response.VehicleInventoryResponse;
import com.swp391.edrive.entity.*;
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

        // Group theo manufacturer
        Map<String, List<ManufacturerInventory>> grouped = inventories.stream()
                .collect(Collectors.groupingBy(inv -> inv.getManufacturer().getManufacturerName()));

        // Chuyển sang DTO
        return grouped.entrySet().stream()
                .map(entry -> {
                    String manufacturerName = entry.getKey();
                    List<ManufacturerInventory> items = entry.getValue();

                    int totalQuantity = items.stream()
                            .mapToInt(ManufacturerInventory::getQuantity)
                            .sum();

                    List<VehicleInventoryResponse> vehicles = items.stream()
                            .map(inv -> VehicleInventoryResponse.builder()
                                    .manufacturerInventoryId(inv.getManufacturerInventoryId())
                                    .vehicleId(inv.getVehicle().getVehicleId())
                                    .vehicleName(inv.getVehicle().getModelName())
                                    .quantity(inv.getQuantity())
                                    .build())
                            .toList();

                    return ManufacturerInventorySummaryResponse.builder()
                            .manufacturerName(manufacturerName)
                            .totalQuantity(totalQuantity)
                            .vehicles(vehicles)
                            .build();
                })
                .toList();
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
