package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.response.DealerInventoryDTO;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.DealerInventory;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.repository.DealerInventoryRepository;

import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.VehicleRepository;
import com.swp391.edrive.service.DealerInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealerInventoryServiceImpl implements DealerInventoryService {

    private final DealerInventoryRepository dealerInventoryRepository;
    private final DealerRepository dealerRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public DealerInventoryDTO updateDealerInventory(Long dealerId, Long vehicleId, int quantity) {
        // Tìm đại lý theo dealerId
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Không thể tìm thấy đại lý với Id: " + dealerId));

        // Tìm xe theo vehicleId
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));

        // Tìm DealerInventory dựa trên dealer và vehicle
        DealerInventory inventory = dealerInventoryRepository.findByDealerAndVehicle(dealer, vehicle)
                .orElseThrow(() -> new RuntimeException("Không thể tìm thấy xe với Id:"));

        // Kiểm tra số lượng nhập vào (validation)
        if (inventory.getQuantity() + quantity < 0) {
            throw new RuntimeException("Không thể cập nhật số lượng xe dưới 0. Số lượng hiện tại: " + inventory.getQuantity());
        }

        // Cập nhật số lượng xe trong kho của đại lý
        inventory.setQuantity(inventory.getQuantity() + quantity);  // Cập nhật số lượng mới
        inventory.setLastUpdated(LocalDateTime.now());

        // Lưu lại thông tin đã cập nhật vào cơ sở dữ liệu
        DealerInventory updatedInventory = dealerInventoryRepository.save(inventory);

        // Trả về DTO của kho xe đại lý đã được cập nhật
        return new DealerInventoryDTO(
                updatedInventory.getVehicle().getVehicleId(),
                updatedInventory.getVehicle().getModelName(),
                updatedInventory.getVehicle().getVersion(),
                updatedInventory.getVehicle().getColor().getColorName(),
                updatedInventory.getQuantity()
        );
    }

    @Override
    public List<DealerInventoryDTO> getDealerInventoryByDealerId(Long dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Dealer not found with id: " + dealerId));

        // Lấy tất cả các DealerInventory của đại lý cụ thể
        List<DealerInventory> inventories = dealerInventoryRepository.findByDealer(dealer);

        // Chuyển đổi thành DTO
        return inventories.stream()
                .map(inv -> new DealerInventoryDTO(
                        inv.getVehicle().getVehicleId(),
                        inv.getVehicle().getModelName(),
                        inv.getVehicle().getVersion(),
                        inv.getVehicle().getColor().getColorName(),
                        inv.getQuantity()
                ))
                .collect(Collectors.toList());
    }

}
