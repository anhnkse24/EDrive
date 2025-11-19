package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.response.DealerInventoryDTO;
import com.swp391.edrive.entity.DealerInventory;
import com.swp391.edrive.repository.DealerInventoryRepository;

import com.swp391.edrive.service.DealerInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealerInventoryServiceImpl implements DealerInventoryService {

    private final DealerInventoryRepository dealerInventoryRepository;

    @Override
    public DealerInventoryDTO updateDealerInventory(Long dealerId, Long vehicleId, int quantity) {
        DealerInventory inventory = dealerInventoryRepository
                .findByDealer_DealerIdAndVehicle_VehicleId(dealerId, vehicleId)
                .orElseThrow(() -> new RuntimeException("DealerInventory not found for dealerId: " + dealerId + " and vehicleId: " + vehicleId));

        inventory.setQuantity(quantity);
        DealerInventory updated = dealerInventoryRepository.save(inventory);

        return new DealerInventoryDTO(
                updated.getVehicle().getModelName(),
                updated.getVehicle().getVersion(),
                updated.getVehicle().getColor() != null ? updated.getVehicle().getColor().getColorName() : null,
                updated.getQuantity()
        );
    }

    @Override
    public List<DealerInventoryDTO> getDealerInventoryByDealerId(Long dealerId) {
        List<DealerInventory> inventories = dealerInventoryRepository.findByDealer_DealerId(dealerId);

        return inventories.stream()
                .map(inv -> new DealerInventoryDTO(
                        inv.getVehicle().getModelName(),
                        inv.getVehicle().getVersion(),
                        inv.getVehicle().getColor() != null ? inv.getVehicle().getColor().getColorName() : null,
                        inv.getQuantity()
                ))
                .collect(Collectors.toList());
    }
}
