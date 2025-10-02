package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "API quản lý danh sách xe")
public class VehicleController {

    private final VehicleService vehicleService; // <-- dùng interface

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

    /** === 1) findVehicleById === */
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

    /** === 2) findVehicleByStatus === */
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

    /** === 3) findVehicleByColor === */
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
}
