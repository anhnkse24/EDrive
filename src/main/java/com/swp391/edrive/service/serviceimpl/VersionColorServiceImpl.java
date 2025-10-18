package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.VersionColorPricePatchRequest;
import com.swp391.edrive.dto.request.VersionColorUpsertRequest;
import com.swp391.edrive.dto.response.ColorBriefResponse;
import com.swp391.edrive.entity.VehicleVersion;
import com.swp391.edrive.entity.VersionColor;
import com.swp391.edrive.repository.VehicleVersionRepository;
import com.swp391.edrive.repository.VersionColorRepository;
import com.swp391.edrive.service.VersionColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VersionColorServiceImpl implements VersionColorService {
    private final VehicleVersionRepository versionRepo;
    private final VersionColorRepository colorRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ColorBriefResponse> list(Long versionId, Boolean active) {
        VehicleVersion v = versionRepo.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle version not found: " + versionId));
        return v.getColors().stream()
                .filter(c -> active == null || Boolean.TRUE.equals(c.getIsActive()) == active)
                .map(this::toDto).toList();
    }

    public ColorBriefResponse create(Long versionId, VersionColorUpsertRequest r) {
        VehicleVersion v = versionRepo.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle version not found: " + versionId));

        String codeNorm = normalize(r.getColorCode());
        if (colorRepo.existsByVersion_IdAndColorCodeIgnoreCaseAndIdNot(versionId, codeNorm, -1L)) {
            // Dùng -1L để “không trùng với ai” về mặt id khi create
            throw new IllegalArgumentException("Color code already exists in this version: " + codeNorm);
        }

        validatePrice(r.getPriceDelta(), r.getPriceOverride());

        VersionColor c = new VersionColor();
        c.setVersion(v);
        c.setColorName(r.getColorName().trim());
        c.setColorCode(codeNorm);
        c.setImageUrl(r.getImageUrl());
        c.setIsActive(r.getActive());
        c.setPriceDelta(r.getPriceDelta());
        c.setPriceOverride(r.getPriceOverride());

        colorRepo.save(c);
        return toDto(c);
    }

    @Override
    public ColorBriefResponse update(Long versionId, Long colorId, VersionColorUpsertRequest r) {
        VersionColor c = mustGet(versionId, colorId);

        // 1) Chuẩn hóa mã mới & cũ (trim + upper)
        String newCodeNorm = normalize(r.getColorCode());
        String oldCodeNorm = normalize(c.getColorCode());

        // 2) Nếu đổi mã (sau chuẩn hóa), kiểm tra trùng – loại trừ chính nó
        if (!oldCodeNorm.equals(newCodeNorm)) {
            boolean exists = colorRepo
                    .existsByVersion_IdAndColorCodeIgnoreCaseAndIdNot(versionId, newCodeNorm, c.getId());
            if (exists) {
                throw new IllegalArgumentException("Color code already exists in this version: " + newCodeNorm);
            }
        }

        // 3) Validate giá như cũ
        validatePrice(r.getPriceDelta(), r.getPriceOverride());

        // 4) Gán field (NHỚ gán code đã normalize)
        c.setColorName(r.getColorName().trim());
        c.setColorCode(newCodeNorm);                 // <-- dùng mã đã normalize
        c.setImageUrl(r.getImageUrl());
        c.setIsActive(r.getActive());
        c.setPriceDelta(r.getPriceDelta());
        c.setPriceOverride(r.getPriceOverride());

        return toDto(c);
    }

    // helper
    private String normalize(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }

    @Override
    public ColorBriefResponse patchPrice(Long versionId, Long colorId, VersionColorPricePatchRequest r) {
        VersionColor c = mustGet(versionId, colorId);
        if (r.getPriceOverride() != null && r.getPriceOverride().signum() <= 0)
            throw new IllegalArgumentException("priceOverride must be > 0");

        if (r.getPriceDelta() != null)   c.setPriceDelta(r.getPriceDelta());
        if (r.getPriceOverride() != null) c.setPriceOverride(r.getPriceOverride());
        return toDto(c);
    }

    @Override
    public ColorBriefResponse activate(Long versionId, Long colorId, boolean active) {
        VersionColor c = mustGet(versionId, colorId);
        c.setIsActive(active);
        return toDto(c);
    }

    @Override
    public void delete(Long versionId, Long colorId) {
        VersionColor c = mustGet(versionId, colorId);
        colorRepo.delete(c);
    }

    // ===== helpers =====
    private VersionColor mustGet(Long versionId, Long colorId) {
        return colorRepo.findById(colorId)
                .filter(c -> c.getVersion() != null && c.getVersion().getId().equals(versionId))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Color not found for versionId=" + versionId + ", colorId=" + colorId));
    }

    private void validatePrice(BigDecimal delta, BigDecimal override) {
        if (override != null && override.signum() <= 0)
            throw new IllegalArgumentException("priceOverride must be > 0");
        // delta có thể âm/dương/null → không cần check
    }

    private ColorBriefResponse toDto(VersionColor vc) {
        return ColorBriefResponse.builder()
                .colorId(vc.getId())
                .colorName(vc.getColorName())
                .colorCode(vc.getColorCode())
                .imageUrl(vc.getImageUrl())
                .active(vc.getIsActive())
                .priceDelta(vc.getPriceDelta())
                .priceOverride(vc.getPriceOverride())
                .retailPrice(vc.retailPrice()) // dùng method sẵn có trong entity
                .build();
    }
}
