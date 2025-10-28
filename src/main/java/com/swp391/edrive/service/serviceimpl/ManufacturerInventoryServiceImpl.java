package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.ManufacturerInventoryRequest;
import com.swp391.edrive.dto.response.InventoryResponse;
import com.swp391.edrive.entity.Manufacturer;
import com.swp391.edrive.entity.ManufacturerInventory;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.repository.ManufacturerInventoryRepository;
import com.swp391.edrive.repository.ManufacturerRepository;
import com.swp391.edrive.repository.VehicleRepository;
import com.swp391.edrive.service.ManufacturerInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManufacturerInventoryServiceImpl implements ManufacturerInventoryService {

    private final ManufacturerInventoryRepository manufacturerInventoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final VehicleRepository vehicleRepository;

    // =========================
    // 🔹 Lấy dữ liệu
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventories() {
        return manufacturerInventoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getById(Long id) {
        ManufacturerInventory inv = manufacturerInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho sản xuất với ID = " + id));
        return toResponse(inv);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getByVehicleId(Long vehicleId) {
        ManufacturerInventory inv = manufacturerInventoryRepository.findByVehicle_VehicleId(vehicleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho sản xuất cho vehicleId = " + vehicleId));
        return toResponse(inv);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getByManufacturerId(Long manufacturerId) {
        return manufacturerInventoryRepository.findByManufacturer_ManufacturerId(manufacturerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public InventoryResponse createInventory(ManufacturerInventoryRequest request) {
        Manufacturer manufacturer = manufacturerRepository.findById(request.getManufacturerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Manufacturer với ID = " + request.getManufacturerId()));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Vehicle với ID = " + request.getVehicleId()));

        ManufacturerInventory inv = ManufacturerInventory.builder()
                .manufacturer(manufacturer)
                .vehicle(vehicle)
                .quantity(request.getQuantity())
                .lastUpdated(LocalDateTime.now())
                .build();

        ManufacturerInventory saved = manufacturerInventoryRepository.save(inv);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(Long id, ManufacturerInventoryRequest request) {
        ManufacturerInventory inv = manufacturerInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho sản xuất với ID = " + id));

        if (request.getQuantity() != null) {
            inv.setQuantity(request.getQuantity());
        }

        if (request.getManufacturerId() != null) {
            Manufacturer manufacturer = manufacturerRepository.findById(request.getManufacturerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Manufacturer với ID = " + request.getManufacturerId()));
            inv.setManufacturer(manufacturer);
        }

        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Vehicle với ID = " + request.getVehicleId()));
            inv.setVehicle(vehicle);
        }

        inv.setLastUpdated(LocalDateTime.now());
        ManufacturerInventory updated = manufacturerInventoryRepository.save(inv);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        if (!manufacturerInventoryRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy kho sản xuất với id = " + id);
        }
        manufacturerInventoryRepository.deleteById(id);
    }

    private InventoryResponse toResponse(ManufacturerInventory inv) {
        return InventoryResponse.builder()
                .inventoryId(inv.getManufacturerInventoryId())
                .ownerId(inv.getManufacturer().getManufacturerId())
                .ownerName(inv.getManufacturer().getManufacturerName())
                .vehicleId(inv.getVehicle().getVehicleId())
                .vehicleModel(inv.getVehicle().getModelName())
                .quantity(inv.getQuantity())
                .lastUpdated(inv.getLastUpdated())
                .build();
    }
}
