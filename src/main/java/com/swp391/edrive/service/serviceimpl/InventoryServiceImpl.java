package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.InventoryRequest;
import com.swp391.edrive.dto.response.InventoryResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.Inventory;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.InventoryRepository;
import com.swp391.edrive.repository.VehicleRepository;
import com.swp391.edrive.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final DealerRepository dealerRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public InventoryResponse createInventory(InventoryRequest req) {
        Dealer dealer = dealerRepository.findById(req.getDealerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý"));
        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe"));

        Inventory inventory = new Inventory();
        inventory.setDealer(dealer);
        inventory.setVehicle(vehicle);
        inventory.setQuantity(req.getQuantity());
        inventory.setLastUpdated(LocalDateTime.now());

        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(Long id, InventoryRequest req) {
        Inventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho xe"));
        inv.setQuantity(req.getQuantity());
        inv.setLastUpdated(LocalDateTime.now());
        return toResponse(inventoryRepository.save(inv));
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(Long id) {
        return toResponse(inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho xe")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoriesByDealer(Long dealerId) {
        return inventoryRepository.findByDealer_DealerId(dealerId)
                .stream().map(this::toResponse).toList();
    }

    private InventoryResponse toResponse(Inventory inv) {
        return InventoryResponse.builder()
                .inventoryId(inv.getInventoryId())
                .dealerId(inv.getDealer().getDealerId())
                .dealerName(inv.getDealer().getDealerName())
                .vehicleId(inv.getVehicle().getVehicleId())
                .vehicleModel(inv.getVehicle().getModelName())
                .quantity(inv.getQuantity())
                .lastUpdated(inv.getLastUpdated())
                .build();
    }
}
