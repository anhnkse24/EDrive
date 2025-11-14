package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ManufacturerInventoryRequest;
import com.swp391.edrive.dto.request.ManufacturerInventoryUpdateRequest;
import com.swp391.edrive.dto.response.ManufacturerInventoryResponse;
import com.swp391.edrive.dto.response.ManufacturerInventorySummaryResponse;
import com.swp391.edrive.service.ManufacturerInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturer-inventory")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@Tag(
        name = "Manufacturer Inventory",
        description = "Quản lý tồn kho của các hãng xe (Manufacturer Inventory CRUD + Summary)"
)
@PreAuthorize("hasRole('ADMIN') or hasRole('EVM_STAFF')")
public class ManufacturerInventoryController {

    private final ManufacturerInventoryService manufacturerInventoryService;

    @Operation(
            summary = "Lấy tồn kho theo ID",
            description = "Tìm và trả về thông tin tồn kho của một hãng theo ID cụ thể"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ManufacturerInventoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturerInventoryService.getById(id));
    }

    @Operation(
            summary = "Tạo mới bản ghi tồn kho",
            description = "Tạo mới một bản ghi tồn kho của hãng sản xuất (Manufacturer Inventory)"
    )
    @PostMapping
    public ResponseEntity<ManufacturerInventoryResponse> create(
            @RequestBody ManufacturerInventoryRequest request) {
        return ResponseEntity.ok(manufacturerInventoryService.create(request));
    }

    @Operation(
            summary = "Cập nhật thông tin tồn kho",
            description = "Cập nhật thông tin bản ghi tồn kho dựa trên ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ManufacturerInventoryResponse> update(
            @PathVariable Long id,
            @RequestBody ManufacturerInventoryUpdateRequest request) {
        return ResponseEntity.ok(manufacturerInventoryService.update(id, request));
    }

    @Operation(
            summary = "Xóa bản ghi tồn kho",
            description = "Xóa bản ghi tồn kho theo ID của manufacturer inventory"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        manufacturerInventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Thống kê tổng hợp tồn kho theo hãng sản xuất",
            description = "Nhóm dữ liệu tồn kho theo tên hãng và trả về số lượng xe tồn kho theo từng hãng"
    )
    @GetMapping("/summary")
    public ResponseEntity<List<ManufacturerInventorySummaryResponse>> getSummary() {
        return ResponseEntity.ok(manufacturerInventoryService.getGroupedByManufacturer());
    }
}
