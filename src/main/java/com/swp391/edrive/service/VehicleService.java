package com.swp391.edrive.service;

import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.enums.VehicleStatus;

import java.util.List;

public interface VehicleService {
    List<Vehicle> getAllVehicles();

    List<VehicleResponse> getAllVehicles(int page, int size);

    VehicleResponse findVehicleById(Long id);

    List<VehicleResponse> findVehicleByStatus(VehicleStatus status, int page, int size);

    List<VehicleResponse> findVehicleByColor(String color, int page, int size);
}
