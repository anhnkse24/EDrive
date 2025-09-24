package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<VehicleResponse> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return vehicles.stream()
                .map(v -> new VehicleResponse(
                        v.getVehicleId(),
                        v.getModelName(),
                        v.getVersion(),
                        v.getColor(),
                        v.getBatteryCapacityKwh(),
                        v.getRangeKm(),
                        v.getMaxSpeedKmh(),
                        v.getChargingTimeHours(),
                        v.getSeatingCapacity(),
                        v.getMotorPowerKw(),
                        v.getWeightKg(),
                        v.getLengthMm(),
                        v.getWidthMm(),
                        v.getHeightMm(),
                        v.getPriceRetail(),
                        v.getStatus().name()
                ))
                .collect(Collectors.toList());
    }
}
