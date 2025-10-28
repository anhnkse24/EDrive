package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.InventoryResponse;
import com.swp391.edrive.service.ManufacturerInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturer-inventory")
@RequiredArgsConstructor
@Tag(
        name = "Manufacturer Inventory",
        description = "API quản lý kho xe của nhà sản xuất (Manufacturer Inventory)"
)
public class ManufacturerInventoryController {

    private final ManufacturerInventoryService manufacturerInventoryService;

    @Operation(summary = "Lấy danh sách toàn bộ kho xe của nhà sản xuất")
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAll() {
        return ResponseEntity.ok(manufacturerInventoryService.getAllInventories());
    }

    @Operation(summary = "Lấy thông tin kho xe theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturerInventoryService.getById(id));
    }

    @Operation(summary = "Lấy kho xe theo vehicleId")
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<InventoryResponse> getByVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(manufacturerInventoryService.getByVehicleId(vehicleId));
    }

    @Operation(summary = "Lấy danh sách kho xe theo manufacturerId")
    @GetMapping("/manufacturer/{manufacturerId}")
    public ResponseEntity<List<InventoryResponse>> getByManufacturer(@PathVariable Long manufacturerId) {
        return ResponseEntity.ok(manufacturerInventoryService.getByManufacturerId(manufacturerId));
    }

    @Operation(summary = "Xóa kho xe của nhà sản xuất theo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        manufacturerInventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
