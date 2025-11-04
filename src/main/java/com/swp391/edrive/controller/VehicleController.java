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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "API quản lý danh sách xe")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;   
    }

    @Operation(summary = "Lấy danh sách tất cả xe")
    @GetMapping
    public ResponseEntity<ResponseObject> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<VehicleResponse> vehicles = vehicleService.getAllVehicles(page, size);
        return ResponseEntity.ok(new ResponseObject(200, "Vehicle list retrieved successfully", vehicles));
    }

    @Operation(summary = "Tìm xe theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        try {
            VehicleResponse vehicle = vehicleService.findVehicleById(id);
            return ResponseEntity.ok(new ResponseObject(200, "Vehicle found", vehicle));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(404, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Tìm xe theo trạng thái")
    @GetMapping("/search/status")
    public ResponseEntity<ResponseObject> findByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        VehicleStatus st;
        try {
            st = VehicleStatus.valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, "Invalid status. Use AVAILABLE or DISCONTINUED", null));
        }

        List<VehicleResponse> vehicles = vehicleService.findVehicleByStatus(st, page, size);
        return ResponseEntity.ok(new ResponseObject(200, "Vehicles by status retrieved", vehicles));
    }

    @Operation(summary = "Tìm xe theo màu")
    @GetMapping("/search/color")
    public ResponseEntity<ResponseObject> findByColor(
            @RequestParam String color,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (color == null || color.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, "Color must not be empty", null));
        }

        List<VehicleResponse> vehicles = vehicleService.findVehicleByColor(color.trim(), page, size);
        return ResponseEntity.ok(new ResponseObject(200, "Vehicles by color retrieved", vehicles));
    }

    @Operation(summary = "Tìm xe theo năm sản xuất (exact hoặc range)")
    @GetMapping("/search/year")
    public ResponseEntity<ResponseObject> findByYear(
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
                        .body(new ResponseObject(400, "Provide either 'year' or both 'fromYear' & 'toYear'", null));
            }

            return ResponseEntity.ok(new ResponseObject(200, "Vehicles by year retrieved", vehicles));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(400, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Tìm xe theo giá (min/max hoặc khoảng)")
    @GetMapping("/search/price")
    public ResponseEntity<ResponseObject> findByPrice(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            List<VehicleResponse> vehicles = vehicleService.findVehicleByPrice(minPrice, maxPrice, page, size);
            return ResponseEntity.ok(new ResponseObject(200, "Vehicles by price retrieved", vehicles));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(400, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Cập nhật thông tin xe")
    @PutMapping("/{id}")
    @SecurityRequirement(name = "api")

    public ResponseEntity<ResponseObject> update(@PathVariable Long id, @Valid @RequestBody VehicleUpsertRequest req) {
        try {
            VehicleResponse updated = vehicleService.updateVehicle(id, req);
            return ResponseEntity.ok(new ResponseObject(200, "Vehicle updated", updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(404, ex.getMessage(), null));
        }
    }

    // ====== DELETE ======
    @Operation(summary = "Xoá xe")
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "api")

    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.ok(new ResponseObject(200, "Vehicle deleted", null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(404, ex.getMessage(), null));
        }
    }

    @PostMapping
    @SecurityRequirement(name = "api")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody VehicleUpsertRequest req) {
        try {
            // Gọi service để tạo xe, nhận danh sách các xe đã tạo
            List<VehicleResponse> createdVehicles = vehicleService.createVehicle(req);

            // Trả về danh sách các xe đã tạo với mã trạng thái CREATED (201)
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(201, "Vehicles created", createdVehicles));

        } catch (IllegalArgumentException ex) {
            // Trả về lỗi nếu có
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseObject(409, ex.getMessage(), null));
        }
    }

}
