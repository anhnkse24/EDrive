package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.VehicleUpsertRequest;
import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.enums.VehicleStatus;

import java.math.BigDecimal;
import java.util.List;

public interface VehicleService {

//    List<VehicleResponse> getAllVehicles(int page, int size);
    List<VehicleResponse> getAllVehicles();

    VehicleResponse findVehicleById(Long id);

    List<VehicleResponse> findVehicleByStatus(VehicleStatus status, int page, int size);

    List<VehicleResponse> findVehicleByColor(String color, int page, int size);

    List<VehicleResponse> findVehicleByManufactureYear(Integer year, int page, int size);

    List<VehicleResponse> findVehicleByManufactureYearRange(Integer fromYear, Integer toYear, int page, int size);

    List<VehicleResponse> findVehicleByPrice(BigDecimal minPrice, BigDecimal maxPrice, int page, int size);

    List<VehicleResponse> createVehicle(VehicleUpsertRequest req);

    VehicleResponse updateVehicle(Long id, VehicleUpsertRequest req);

    void deleteVehicle(Long id);
}
