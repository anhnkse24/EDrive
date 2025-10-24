package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.InventoryRequest;
import com.swp391.edrive.dto.response.InventoryResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "Quản lý kho xe của đại lý")
public class InventoryController {

    private final InventoryService inventoryService;

    // 🟢 CREATE
    @Operation(summary = "Thêm xe vào kho đại lý")
    @PostMapping
    public ResponseEntity<ResponseObject> createInventory(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse created = inventoryService.createInventory(request);
        return ResponseEntity.ok(
                new ResponseObject(200, "Thêm xe vào kho thành công", created)
        );
    }

    // 🟡 UPDATE
    @Operation(summary = "Cập nhật thông tin xe trong kho")
    @PutMapping("/{inventoryId}")
    public ResponseEntity<ResponseObject> updateInventory(
            @PathVariable Long inventoryId,
            @Valid @RequestBody InventoryRequest request) {
        InventoryResponse updated = inventoryService.updateInventory(inventoryId, request);
        return ResponseEntity.ok(
                new ResponseObject(200, "Cập nhật kho thành công", updated)
        );
    }

    // 🔴 DELETE
    @Operation(summary = "Xóa xe khỏi kho")
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<ResponseObject> deleteInventory(@PathVariable Long inventoryId) {
        inventoryService.deleteInventory(inventoryId);
        return ResponseEntity.ok(
                new ResponseObject(200, "Xóa xe khỏi kho thành công", null)
        );
    }

    // 🔵 GET BY ID
    @Operation(summary = "Xem chi tiết 1 xe trong kho")
    @GetMapping("/{inventoryId}")
    public ResponseEntity<ResponseObject> getInventoryById(@PathVariable Long inventoryId) {
        InventoryResponse inventory = inventoryService.getInventoryById(inventoryId);
        return ResponseEntity.ok(
                new ResponseObject(200, "Lấy thông tin kho thành công", inventory)
        );
    }

    // 🟣 GET ALL
    @Operation(summary = "Lấy danh sách toàn bộ kho xe")
    @GetMapping
    public ResponseEntity<ResponseObject> getAllInventories() {
        List<InventoryResponse> list = inventoryService.getAllInventories();
        return ResponseEntity.ok(
                new ResponseObject(200, "Lấy danh sách kho thành công", list)
        );
    }

    // 🔵 GET BY DEALER
    @Operation(summary = "Lấy danh sách xe trong kho theo đại lý")
    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<ResponseObject> getInventoriesByDealer(@PathVariable Long dealerId) {
        List<InventoryResponse> list = inventoryService.getInventoriesByDealer(dealerId);
        return ResponseEntity.ok(
                new ResponseObject(200, "Lấy danh sách xe trong kho đại lý thành công", list)
        );
    }
}
