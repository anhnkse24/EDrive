package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ManufacturerInventoryRequest;
import com.swp391.edrive.dto.request.ManufacturerInventoryUpdateRequest;
import com.swp391.edrive.dto.response.ManufacturerInventoryResponse;
import com.swp391.edrive.dto.response.ManufacturerInventorySummaryResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.ManufacturerInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class ManufacturerInventoryController {

    private final ManufacturerInventoryService manufacturerInventoryService;

    @Operation(
            summary = "Lấy tồn kho theo ID",
            description = "Tìm và trả về thông tin tồn kho của một hãng theo ID cụ thể"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<ManufacturerInventoryResponse>> getById(@PathVariable Long id) {

        ManufacturerInventoryResponse data = manufacturerInventoryService.getById(id);

        return ResponseEntity.ok(
                ResponseObject.<ManufacturerInventoryResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy danh sách kho theo đại lí thành công")
                        .data(data)
                        .build()
        );
    }

    @Operation(
            summary = "Tạo mới bản ghi tồn kho",
            description = "Tạo mới một bản ghi tồn kho của hãng sản xuất (Manufacturer Inventory)"
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<ManufacturerInventoryResponse>> create(
            @RequestBody ManufacturerInventoryRequest request) {

        ManufacturerInventoryResponse data = manufacturerInventoryService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.<ManufacturerInventoryResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Inventory created successfully")
                        .data(data)
                        .build()
        );
    }

    @Operation(
            summary = "Cập nhật thông tin tồn kho",
            description = "Cập nhật thông tin bản ghi tồn kho dựa trên ID"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<ManufacturerInventoryResponse>> update(
            @PathVariable Long id,
            @RequestBody ManufacturerInventoryUpdateRequest request) {

        ManufacturerInventoryResponse data = manufacturerInventoryService.update(id, request);

        return ResponseEntity.ok(
                ResponseObject.<ManufacturerInventoryResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Chỉnh sửa kho thành công")
                        .data(data)
                        .build()
        );
    }

    @Operation(
            summary = "Xóa bản ghi tồn kho",
            description = "Xóa bản ghi tồn kho theo ID của manufacturer inventory"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<Void>> delete(@PathVariable Long id) {

        manufacturerInventoryService.delete(id);

        return ResponseEntity.ok(
                ResponseObject.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Xoá kho thành công")
                        .data(null)
                        .build()
        );
    }

    @Operation(
            summary = "Thống kê tổng hợp tồn kho theo hãng sản xuất",
            description = "Nhóm dữ liệu tồn kho theo tên hãng và trả về số lượng xe tồn kho theo từng hãng"
    )
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject<List<ManufacturerInventorySummaryResponse>>> getSummary() {

        List<ManufacturerInventorySummaryResponse> data = manufacturerInventoryService.getGroupedByManufacturer();

        return ResponseEntity.ok(
                ResponseObject.<List<ManufacturerInventorySummaryResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Tổng danh sách kho")
                        .data(data)
                        .build()
        );
    }
}
