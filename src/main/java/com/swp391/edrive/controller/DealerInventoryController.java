package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.DealerInventoryAdjustRequest;
import com.swp391.edrive.dto.request.DealerInventoryUpdateRequest;
import com.swp391.edrive.dto.request.DealerInventoryUpsertRequest;
import com.swp391.edrive.dto.response.DealerInventoryResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dealers/{dealerId}/inventory")
@Tag(name = "Dealer Inventory", description = "Quản lý tồn kho tại đại lý (dùng cho Test Drive & bán hàng)")
public class DealerInventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ResponseObject> list(@PathVariable Long dealerId,
                                               @RequestParam(required = false) Long versionId,
                                               @RequestParam(required = false) Long versionColorId,
                                               @RequestParam(defaultValue = "false") boolean onlyAvailable) {
        var data = inventoryService.list(dealerId, versionId, versionColorId, onlyAvailable);
        return ResponseEntity.ok(new ResponseObject(200, "Dealer inventory retrieved", data));
    }

    @GetMapping("/capacity")
    public ResponseEntity<ResponseObject> capacity(@PathVariable Long dealerId,
                                                   @RequestParam(required = false) Long versionId,
                                                   @RequestParam(required = false) Long versionColorId) {
        if (versionColorId != null) {
            int cap = inventoryService.getDemoCapacityByVersionColor(dealerId, versionColorId);
            return ResponseEntity.ok(new ResponseObject(200, "Capacity by versionColor retrieved", cap));
        }
        if (versionId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(400, "Either versionId or versionColorId is required", null));
        }
        int cap = inventoryService.getDemoCapacityByVersion(dealerId, versionId);
        return ResponseEntity.ok(new ResponseObject(200, "Capacity by version retrieved", cap));
    }

    @PostMapping
    public ResponseEntity<ResponseObject> upsert(@PathVariable Long dealerId,
                                                 @Valid @RequestBody DealerInventoryUpsertRequest req) {
        var data = inventoryService.upsert(dealerId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Dealer inventory upserted", data));
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<ResponseObject> update(@PathVariable Long dealerId,
                                                 @PathVariable Long inventoryId,
                                                 @Valid @RequestBody DealerInventoryUpdateRequest req) {
        var data = inventoryService.update(dealerId, inventoryId, req);
        return ResponseEntity.ok(new ResponseObject(200, "Dealer inventory updated", data));
    }

    @PatchMapping("/{inventoryId}/adjust")
    public ResponseEntity<ResponseObject> adjust(@PathVariable Long dealerId,
                                                 @PathVariable Long inventoryId,
                                                 @Valid @RequestBody DealerInventoryAdjustRequest req) {
        var data = inventoryService.adjust(dealerId, inventoryId, req);
        return ResponseEntity.ok(new ResponseObject(200, "Dealer inventory adjusted", data));
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long dealerId,
                                                 @PathVariable Long inventoryId) {
        inventoryService.delete(dealerId, inventoryId);
        return ResponseEntity.ok(new ResponseObject(200, "Dealer inventory deleted", null));
    }
}