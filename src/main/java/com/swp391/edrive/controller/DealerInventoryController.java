package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.DealerInventoryDTO;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.DealerInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dealer-inventory")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class DealerInventoryController {

    private final DealerInventoryService dealerInventoryService;

    @Operation(summary = "Cập nhật số lượng xe trong kho đại lý")
    @PutMapping("/update/{dealerId}/{vehicleId}")
    public ResponseEntity<ResponseObject> updateDealerInventory(
            @PathVariable Long dealerId,  // ID của Dealer
            @PathVariable Long vehicleId, // ID của Vehicle
            @RequestParam int quantity) { // Số lượng xe mới
        DealerInventoryDTO updatedInventory = dealerInventoryService.updateDealerInventory(dealerId, vehicleId, quantity);
        return ResponseEntity.ok(
                new ResponseObject(200, "Cập nhật số lượng xe thành công", updatedInventory)
        );
    }

    @Operation(summary = "Lấy thông tin kho đại lý theo ID đại lý")
    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<ResponseObject> getDealerInventoryByDealerId(@PathVariable Long dealerId) {
        List<DealerInventoryDTO> inventories = dealerInventoryService.getDealerInventoryByDealerId(dealerId);
        return ResponseEntity.ok(
                new ResponseObject(200, "Lấy thông tin kho đại lý thành công", inventories)
        );
    }

}
