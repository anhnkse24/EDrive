package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.VehicleUpsertRequest;
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
    public List<VehicleResponse> getAllVehicles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> vehicles = vehicleRepository.findAll(pageable);
        return vehicles.stream().map(this::toResponse).toList();
    }

    @Override
    public VehicleResponse findVehicleById(Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id=" + id));
        return toResponse(v);
    }

    @Override
    public List<VehicleResponse> findVehicleByStatus(VehicleStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByStatus(status, pageable);
        return result.stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByColor(String color, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByColorIgnoreCaseContaining(color, pageable);
        return result.stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByManufactureYear(Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleRepository.findByManufactureYear(year, pageable);
        return result.stream().map(this::toResponse).toList();
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
        return result.stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByPrice(Double minPrice, Double maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (minPrice == null && maxPrice == null) {
            throw new IllegalArgumentException("At least one of minPrice or maxPrice must be provided");
        }
        Page<Vehicle> result;
        if (minPrice != null && maxPrice != null) {
            if (minPrice > maxPrice) throw new IllegalArgumentException("minPrice must be <= maxPrice");
            result = vehicleRepository.findByPriceRetailBetween(minPrice, maxPrice, pageable);
        } else if (minPrice != null) {
            result = vehicleRepository.findByPriceRetailGreaterThanEqual(minPrice, pageable);
        } else {
            result = vehicleRepository.findByPriceRetailLessThanEqual(maxPrice, pageable);
        }
        return result.stream().map(this::toResponse).toList();
    }

    // === CREATE ===
    @Override
    @Transactional
    public VehicleResponse createVehicle(VehicleUpsertRequest req) {
        Vehicle v = new Vehicle();
        apply(v, req);
        v = vehicleRepository.save(v);
        return toResponse(v);
    }

    // === UPDATE ===
    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleUpsertRequest req) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id=" + id));
        apply(v, req);
        v = vehicleRepository.save(v);
        return toResponse(v);
    }

    // === DELETE ===
    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id=" + id));
        vehicleRepository.delete(v);
    }

    // === mapping helpers ===
    private void apply(Vehicle v, VehicleUpsertRequest r) {
        v.setModelName(r.getModelName());
        v.setVersion(r.getVersion());
        v.setColor(r.getColor());
        v.setBatteryCapacityKwh(r.getBatteryCapacityKwh());
        v.setRangeKm(r.getRangeKm());
        v.setMaxSpeedKmh(r.getMaxSpeedKmh());
        v.setChargingTimeHours(r.getChargingTimeHours());
        v.setSeatingCapacity(r.getSeatingCapacity());
        v.setMotorPowerKw(r.getMotorPowerKw());
        v.setWeightKg(r.getWeightKg());
        v.setLengthMm(r.getLengthMm());
        v.setWidthMm(r.getWidthMm());
        v.setHeightMm(r.getHeightMm());
        v.setPriceRetail(r.getPriceRetail());
        v.setStatus(r.getStatus());
        v.setManufactureYear(r.getManufactureYear());
    }

    private VehicleResponse toResponse(Vehicle v) {
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
