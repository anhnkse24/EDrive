package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.VehicleVersionUpsertRequest;
import com.swp391.edrive.dto.response.VehicleVersionResponse;
import com.swp391.edrive.entity.VehicleModel;
import com.swp391.edrive.entity.VehicleVersion;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.repository.VehicleModelRepository;
import com.swp391.edrive.repository.VehicleVersionRepository;
import com.swp391.edrive.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {
    private final VehicleVersionRepository versionRepo;
    private final VehicleModelRepository modelRepo;

    @Override
    public List<VehicleVersionResponse> getAllVehicles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleVersion> p = versionRepo.findAll(pageable);
        return p.stream().map(this::toResponse).toList();
    }

    @Override
    public VehicleVersionResponse findVehicleById(Long id) {
        VehicleVersion v = versionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle version not found with id=" + id));
        return toResponse(v);
    }

    @Override
    public List<VehicleVersionResponse> findVehicleByStatus(VehicleStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return versionRepo.findByStatus(status, pageable).stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleVersionResponse> findVehicleByColor(String color, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return versionRepo.findDistinctByColors_ColorNameContainingIgnoreCase(color, pageable)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleVersionResponse> findVehicleByManufactureYear(Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return versionRepo.findByManufactureYear(year, pageable).stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleVersionResponse> findVehicleByManufactureYearRange(Integer fromYear, Integer toYear, int page, int size) {
        if (fromYear == null || toYear == null) throw new IllegalArgumentException("fromYear and toYear are required");
        if (fromYear > toYear) throw new IllegalArgumentException("fromYear must be <= toYear");

        Pageable pageable = PageRequest.of(page, size);
        return versionRepo.findByManufactureYearBetween(fromYear, toYear, pageable)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<VehicleVersionResponse> findVehicleByPrice(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        if (minPrice == null && maxPrice == null)
            throw new IllegalArgumentException("At least one of minPrice or maxPrice must be provided");

        Pageable pageable = PageRequest.of(page, size);

        if (minPrice != null && maxPrice != null) {
            if (minPrice.compareTo(maxPrice) > 0)
                throw new IllegalArgumentException("minPrice must be <= maxPrice");
            return versionRepo.findByBasePriceBetween(minPrice, maxPrice, pageable)
                    .stream().map(this::toResponse).toList();
        } else if (minPrice != null) {
            return versionRepo.findByBasePriceGreaterThanEqual(minPrice, pageable)
                    .stream().map(this::toResponse).toList();
        } else {
            return versionRepo.findByBasePriceLessThanEqual(maxPrice, pageable)
                    .stream().map(this::toResponse).toList();
        }
    }

    // ===== CREATE =====
    @Override
    @Transactional
    public VehicleVersionResponse createVehicle(VehicleVersionUpsertRequest req) {
        VehicleModel model = modelRepo.findById(req.getModelId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle model not found with id=" + req.getModelId()));
        VehicleVersion v = new VehicleVersion();
        v.setModel(model);
        apply(v, req);
        v = versionRepo.save(v);
        return toResponse(v);
    }

    // ===== UPDATE =====
    @Override
    @Transactional
    public VehicleVersionResponse updateVehicle(Long id, VehicleVersionUpsertRequest req) {
        VehicleVersion v = versionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle version not found with id=" + id));
        // nếu cho phép đổi model:
        if (req.getModelId() != null && (v.getModel() == null || !v.getModel().getId().equals(req.getModelId()))) {
            VehicleModel model = modelRepo.findById(req.getModelId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle model not found with id=" + req.getModelId()));
            v.setModel(model);
        }
        apply(v, req);
        v = versionRepo.save(v);
        return toResponse(v);
    }

    // ===== DELETE =====
    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        VehicleVersion v = versionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle version not found with id=" + id));
        versionRepo.delete(v);
    }

    // ===== MAPPERS =====
    private void apply(VehicleVersion v, VehicleVersionUpsertRequest r) {
        v.setVersionName(r.getVersionName());
        v.setBatteryCapacityKwh(r.getBatteryCapacityKwh());
        v.setRangeKm(r.getRangeKm());
        v.setMaxSpeedKmh(r.getMaxSpeedKmh());
        v.setChargingTimeHours(r.getChargingTimeHours().floatValue()); // DTO BigDecimal -> entity Float
        v.setSeatingCapacity(r.getSeatingCapacity());
        v.setMotorPowerKw(r.getMotorPowerKw());
        v.setWeightKg(r.getWeightKg());
        v.setLengthMm(r.getLengthMm());
        v.setWidthMm(r.getWidthMm());
        v.setHeightMm(r.getHeightMm());
        v.setBasePrice(r.getBasePrice());
        v.setStatus(r.getStatus());
        v.setManufactureYear(r.getManufactureYear());
    }

    private VehicleVersionResponse toResponse(VehicleVersion v) {
        return new VehicleVersionResponse(
                v.getId(),
                v.getModel() != null ? v.getModel().getId() : null,
                v.getModel() != null ? v.getModel().getModelName() : null,
                v.getVersionName(),
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
                v.getBasePrice(),
                v.getStatus() != null ? v.getStatus().name() : null,
                v.getManufactureYear()
        );
    }
}
