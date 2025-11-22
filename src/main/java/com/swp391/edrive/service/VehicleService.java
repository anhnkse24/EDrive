package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.VehicleUpsertRequest;
import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.enums.VehicleStatus;
import org.springframework.web.multipart.MultipartFile;

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

    List<VehicleResponse> createVehicleWithImages(VehicleUpsertRequest req, List<MultipartFile> images);

    VehicleResponse updateVehicle(Long id, VehicleUpsertRequest req);

    void deleteVehicle(Long id);

    // Upload image API - upload and update vehicle's image
    VehicleResponse uploadVehicleImage(Long vehicleId, MultipartFile image);
}
