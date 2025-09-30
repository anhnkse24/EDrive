package com.swp391.edrive.service;

import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /** GET ALL (đã có) */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<VehicleResponse> getAllVehicles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> vehicles = vehicleRepository.findAll(pageable);
        return vehicles.stream().map(this::convertToVehicleResponse).toList();
    }

    /** === 1) findVehicleById === */
    public VehicleResponse findVehicleById(Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id=" + id));
        return convertToVehicleResponse(v);
    }

    /** === 2) findVehicleByStatus (có phân trang) === */
    public List<VehicleResponse> findVehicleByStatus(VehicleStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByStatus(status, pageable);
        return result.stream().map(this::convertToVehicleResponse).toList();
    }

    /** === 3) findVehicleByColor (contains + ignore case, có phân trang) === */
    public List<VehicleResponse> findVehicleByColor(String color, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByColorIgnoreCaseContaining(color, pageable);
        return result.stream().map(this::convertToVehicleResponse).toList();
    }

    /** mapper */
    private VehicleResponse convertToVehicleResponse(Vehicle v) {
        return new VehicleResponse(
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
                v.getStatus() != null ? v.getStatus().name() : null
        );
    }
}
