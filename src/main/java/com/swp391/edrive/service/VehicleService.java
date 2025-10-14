package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.VehicleVersionUpsertRequest;
import com.swp391.edrive.dto.response.VehicleVersionResponse;
import com.swp391.edrive.enums.VehicleStatus;

import java.math.BigDecimal;
import java.util.List;

public interface VehicleService {

    List<VehicleVersionResponse> getAllVehicles(int page, int size);

    VehicleVersionResponse findVehicleById(Long id);

    List<VehicleVersionResponse> findVehicleByStatus(VehicleStatus status, int page, int size);

    List<VehicleVersionResponse> findVehicleByColor(String color, int page, int size);

    List<VehicleVersionResponse> findVehicleByManufactureYear(Integer year, int page, int size);

    List<VehicleVersionResponse> findVehicleByManufactureYearRange(Integer fromYear, Integer toYear, int page, int size);

    List<VehicleVersionResponse> findVehicleByPrice(BigDecimal minPrice, BigDecimal maxPrice, int page, int size);

    VehicleVersionResponse createVehicle(VehicleVersionUpsertRequest req);

    VehicleVersionResponse updateVehicle(Long id, VehicleVersionUpsertRequest req);

    void deleteVehicle(Long id);
}
