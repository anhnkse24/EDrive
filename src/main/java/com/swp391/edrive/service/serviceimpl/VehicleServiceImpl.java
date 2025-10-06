package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.repository.VehicleRepository;
import com.swp391.edrive.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<VehicleResponse> getAllVehicles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> vehicles = vehicleRepository.findAll(pageable);
        return vehicles.stream().map(this::convertToVehicleResponse).toList();
    }

    @Override
    public VehicleResponse findVehicleById(Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id=" + id));
        return convertToVehicleResponse(v);
    }

    @Override
    public List<VehicleResponse> findVehicleByStatus(VehicleStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByStatus(status, pageable);
        return result.stream().map(this::convertToVehicleResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByColor(String color, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByColorIgnoreCaseContaining(color, pageable);
        return result.stream().map(this::convertToVehicleResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByManufactureYear(Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByManufactureYear(year, pageable);
        return result.stream().map(this::convertToVehicleResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByManufactureYearRange(Integer fromYear, Integer toYear, int page, int size) {
        if (fromYear == null || toYear == null) {
            throw new IllegalArgumentException("fromYear and toYear are required");
        }
        if (fromYear > toYear) {
            throw new IllegalArgumentException("fromYear must be <= toYear");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByManufactureYearBetween(fromYear, toYear, pageable);
        return result.stream().map(this::convertToVehicleResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByPrice(Double minPrice, Double maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (minPrice == null && maxPrice == null) {
            throw new IllegalArgumentException("At least one of minPrice or maxPrice must be provided");
        }

        Page<Vehicle> result;
        if (minPrice != null && maxPrice != null) {
            if (minPrice > maxPrice) {
                throw new IllegalArgumentException("minPrice must be <= maxPrice");
            }
            result = vehicleRepository.findByPriceRetailBetween(minPrice, maxPrice, pageable);
        } else if (minPrice != null) {
            result = vehicleRepository.findByPriceRetailGreaterThanEqual(minPrice, pageable);
        } else {
            result = vehicleRepository.findByPriceRetailLessThanEqual(maxPrice, pageable);
        }

        return result.stream().map(this::convertToVehicleResponse).toList();
    }

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
                v.getStatus() != null ? v.getStatus().name() : null,
                v.getManufactureYear()
        );
    }
}
