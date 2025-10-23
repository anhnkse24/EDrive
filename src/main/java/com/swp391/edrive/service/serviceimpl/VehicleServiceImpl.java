package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.VehicleVersionUpsertRequest;
import com.swp391.edrive.dto.response.ColorBriefResponse;
import com.swp391.edrive.dto.response.VehicleResponse;
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
    public List<VehicleResponse> getAllVehicles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleVersion> p = versionRepo.findAll(pageable);
        return p.stream().map(this::mapToVehicleResponse).toList();
    }

    @Override
    public VehicleResponse findVehicleById(Long id) {
        VehicleVersion v = versionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle version not found with id=" + id));
        return mapToVehicleResponse(v);
    }

    @Override
    public List<VehicleResponse> findVehicleByStatus(VehicleStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return versionRepo.findByStatus(status, pageable)
                .stream().map(this::mapToVehicleResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByColor(String color, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return versionRepo.findDistinctByColors_ColorNameContainingIgnoreCase(color, pageable)
                .stream().map(this::mapToVehicleResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByManufactureYear(Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return versionRepo.findByManufactureYear(year, pageable)
                .stream().map(this::mapToVehicleResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByManufactureYearRange(Integer fromYear, Integer toYear, int page, int size) {
        if (fromYear == null || toYear == null) throw new IllegalArgumentException("fromYear and toYear are required");
        if (fromYear > toYear) throw new IllegalArgumentException("fromYear must be <= toYear");

        Pageable pageable = PageRequest.of(page, size);
        return versionRepo.findByManufactureYearBetween(fromYear, toYear, pageable)
                .stream().map(this::mapToVehicleResponse).toList();
    }

    @Override
    public List<VehicleResponse> findVehicleByPrice(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        if (minPrice == null && maxPrice == null)
            throw new IllegalArgumentException("At least one of minPrice or maxPrice must be provided");

        Pageable pageable = PageRequest.of(page, size);

        if (minPrice != null && maxPrice != null) {
            if (minPrice.compareTo(maxPrice) > 0)
                throw new IllegalArgumentException("minPrice must be <= maxPrice");
            return versionRepo.findByBasePriceBetween(minPrice, maxPrice, pageable)
                    .stream().map(this::mapToVehicleResponse).toList();
        } else if (minPrice != null) {
            return versionRepo.findByBasePriceGreaterThanEqual(minPrice, pageable)
                    .stream().map(this::mapToVehicleResponse).toList();
        } else {
            return versionRepo.findByBasePriceLessThanEqual(maxPrice, pageable)
                    .stream().map(this::mapToVehicleResponse).toList();
        }
    }

    @Override
    @Transactional
    public VehicleResponse createVehicle(VehicleVersionUpsertRequest req) {
        VehicleModel model = modelRepo.findById(req.getModelId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle model not found with id=" + req.getModelId()));
        VehicleVersion v = new VehicleVersion();
        v.setModel(model);
        apply(v, req);
        v = versionRepo.save(v);
        return mapToVehicleResponse(v);
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleVersionUpsertRequest req) {
        VehicleVersion v = versionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle version not found with id=" + id));
        if (req.getModelId() != null && v.getModel() != null && !v.getModel().getId().equals(req.getModelId())) {
            throw new IllegalArgumentException("Changing modelId of an existing vehicle version is not allowed");
        }
        apply(v, req);
        v = versionRepo.save(v);
        return mapToVehicleResponse(v);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long versionId) {
        VehicleVersion version = versionRepo.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle version not found with id=" + versionId));

        version.setStatus(VehicleStatus.DISCONTINUED);

        if (version.getColors() != null) {
            version.getColors().forEach(c -> c.setIsActive(false));
        }

        versionRepo.save(version);
    }


    private void apply(VehicleVersion v, VehicleVersionUpsertRequest r) {
        v.setVersionName(r.getVersionName());
        v.setBatteryCapacityKwh(r.getBatteryCapacityKwh());
        v.setRangeKm(r.getRangeKm());
        v.setMaxSpeedKmh(r.getMaxSpeedKmh());
        v.setChargingTimeHours(r.getChargingTimeHours() != null ? r.getChargingTimeHours().floatValue() : null);
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

    // ===== Mapper sang VehicleResponse (có colors) =====
    private VehicleResponse mapToVehicleResponse(VehicleVersion v) {
        return VehicleResponse.builder()
                .versionId(v.getId())
                .modelId(v.getModel() != null ? v.getModel().getId() : null)
                .modelName(v.getModel() != null ? v.getModel().getModelName() : null)
                .versionName(v.getVersionName())
                .batteryCapacityKwh(v.getBatteryCapacityKwh())
                .rangeKm(v.getRangeKm())
                .maxSpeedKmh(v.getMaxSpeedKmh())
                .chargingTimeHours(v.getChargingTimeHours())
                .seatingCapacity(v.getSeatingCapacity())
                .motorPowerKw(v.getMotorPowerKw())
                .weightKg(v.getWeightKg())
                .lengthMm(v.getLengthMm())
                .widthMm(v.getWidthMm())
                .heightMm(v.getHeightMm())
                .basePrice(v.getBasePrice())
                .manufactureYear(v.getManufactureYear())
                .status(v.getStatus()) // DTO dùng enum VehicleStatus
                .colors(v.getColors() == null ? List.of()
                        : v.getColors().stream()
                        .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                        .map(vc -> ColorBriefResponse.builder()
                                .colorId(vc.getId())
                                .colorName(vc.getColorName())
                                .colorCode(vc.getColorCode())
                                .imageUrl(vc.getImageUrl())
                                .active(vc.getIsActive())
                                .priceDelta(vc.getPriceDelta())
                                .priceOverride(vc.getPriceOverride())
                                .retailPrice(vc.retailPrice())
                                .build())
                        .toList())
                .build();
    }
}
