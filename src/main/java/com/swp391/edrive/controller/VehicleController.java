package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.VehicleUpsertRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "API quản lý danh sách xe")
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "Lấy danh sách tất cả xe")
    @GetMapping
    public ResponseEntity<ResponseObject<List<VehicleResponse>>> getAllVehicles() {
        List<VehicleResponse> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Lấy danh sách xe thành công", vehicles)
        );
    }

    @Operation(summary = "Tìm xe theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<VehicleResponse>> findById(@PathVariable Long id) {
        try {
            VehicleResponse vehicle = vehicleService.findVehicleById(id);
            return ResponseEntity.ok(
                    new ResponseObject<>(200, "Tìm xe thành công", vehicle)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(404, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Tìm xe theo trạng thái")
    @GetMapping("/search/status")
    public ResponseEntity<ResponseObject<List<VehicleResponse>>> findByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        VehicleStatus st;
        try {
            st = VehicleStatus.valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject<>(400, "Trạng thái không hợp lệ. Chỉ chấp nhận AVAILABLE hoặc DISCONTINUED", null));
        }

        List<VehicleResponse> vehicles = vehicleService.findVehicleByStatus(st, page, size);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Lấy xe theo trạng thái thành công", vehicles)
        );
    }

    @Operation(summary = "Tìm xe theo màu")
    @GetMapping("/search/color")
    public ResponseEntity<ResponseObject<List<VehicleResponse>>> findByColor(
            @RequestParam String color,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (color == null || color.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject<>(400, "Màu sắc không được để trống", null));
        }

        List<VehicleResponse> vehicles = vehicleService.findVehicleByColor(color.trim(), page, size);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Lấy xe theo màu thành công", vehicles)
        );
    }

    @Operation(summary = "Tìm xe theo năm sản xuất (exact hoặc range)")
    @GetMapping("/search/year")
    public ResponseEntity<ResponseObject<List<VehicleResponse>>> findByYear(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            List<VehicleResponse> vehicles;

            if (year != null) {
                vehicles = vehicleService.findVehicleByManufactureYear(year, page, size);
            } else if (fromYear != null && toYear != null) {
                vehicles = vehicleService.findVehicleByManufactureYearRange(fromYear, toYear, page, size);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject<>(400, "Vui lòng cung cấp 'year' hoặc cả 'fromYear' và 'toYear'", null));
            }

            return ResponseEntity.ok(
                    new ResponseObject<>(200, "Lấy xe theo năm sản xuất thành công", vehicles)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(400, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Tìm xe theo giá (min/max hoặc khoảng)")
    @GetMapping("/search/price")
    public ResponseEntity<ResponseObject<List<VehicleResponse>>> findByPrice(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            List<VehicleResponse> vehicles = vehicleService.findVehicleByPrice(minPrice, maxPrice, page, size);
            return ResponseEntity.ok(
                    new ResponseObject<>(200, "Lấy xe theo giá thành công", vehicles)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(400, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Cập nhật thông tin xe")
    @PutMapping("/{id}")
    @SecurityRequirement(name = "api")
    public ResponseEntity<ResponseObject<VehicleResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody VehicleUpsertRequest req) {
        try {
            VehicleResponse updated = vehicleService.updateVehicle(id, req);
            return ResponseEntity.ok(
                    new ResponseObject<>(200, "Cập nhật xe thành công", updated)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(404, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Xóa xe")
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "api")
    public ResponseEntity<ResponseObject<Void>> delete(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.ok(
                    new ResponseObject<>(200, "Xóa xe thành công", null)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(404, ex.getMessage(), null));
        }
    }

    @PostMapping
    @SecurityRequirement(name = "api")
    public ResponseEntity<ResponseObject<List<VehicleResponse>>> create(@Valid @RequestBody VehicleUpsertRequest req) {
        try {
            List<VehicleResponse> createdVehicles = vehicleService.createVehicle(req);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject<>(201, "Tạo xe thành công", createdVehicles));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseObject<>(409, ex.getMessage(), null));
        }
    }
}
