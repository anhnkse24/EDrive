package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.DealerInventoryDTO;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.DealerInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<DealerInventoryDTO>> updateDealerInventory(
            @PathVariable Long dealerId,
            @PathVariable Long vehicleId,
            @RequestParam int quantity) {

        DealerInventoryDTO updatedInventory =
                dealerInventoryService.updateDealerInventory(dealerId, vehicleId, quantity);

        return ResponseEntity.ok(
                ResponseObject.<DealerInventoryDTO>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Cập nhật số lượng xe thành công")
                        .data(updatedInventory)
                        .build()
        );
    }

    @Operation(summary = "Lấy thông tin kho đại lý theo ID đại lý")
    @GetMapping("/dealer/{dealerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALER_MANAGER')")
    public ResponseEntity<ResponseObject<List<DealerInventoryDTO>>> getDealerInventoryByDealerId(
            @PathVariable Long dealerId) {

        List<DealerInventoryDTO> inventories =
                dealerInventoryService.getDealerInventoryByDealerId(dealerId);

        return ResponseEntity.ok(
                ResponseObject.<List<DealerInventoryDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy thông tin kho đại lý thành công")
                        .data(inventories)
                        .build()
        );
    }

}
