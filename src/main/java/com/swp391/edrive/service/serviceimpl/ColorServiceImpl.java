package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.ColorRequest;
import com.swp391.edrive.dto.response.ColorResponse;
import com.swp391.edrive.entity.Color;
import com.swp391.edrive.repository.ColorRepository;
import com.swp391.edrive.repository.VehicleRepository;
import com.swp391.edrive.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepo;
    private final VehicleRepository vehicleRepo;

    @Override
    @Transactional
    public ColorResponse create(ColorRequest req) {
        String name = req.getColorName().trim();
        String hex  = req.getHexCode() != null ? req.getHexCode().toUpperCase() : null;

        colorRepo.findByColorNameIgnoreCase(name)
                .ifPresent(c -> { throw new IllegalArgumentException("Tên màu đã tồn tại"); });
        if (hex != null) {
            colorRepo.findByHexCodeIgnoreCase(hex)
                    .ifPresent(c -> { throw new IllegalArgumentException("Mã hex đã tồn tại"); });
        }

        Color c = Color.builder().colorName(name).hexCode(hex).build();
        c = colorRepo.save(c);
        return toResponse(c, false);
    }

    @Override
    @Transactional
    public ColorResponse update(Long id, ColorRequest req) {
        Color c = colorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy màu"));

        String name = req.getColorName().trim();
        String hex  = req.getHexCode() != null ? req.getHexCode().toUpperCase() : null;

        colorRepo.findByColorNameIgnoreCase(name)
                .filter(x -> !x.getColorId().equals(id))
                .ifPresent(x -> { throw new IllegalArgumentException("Tên màu đã tồn tại"); });

        if (hex != null) {
            colorRepo.findByHexCodeIgnoreCase(hex)
                    .filter(x -> !x.getColorId().equals(id))
                    .ifPresent(x -> { throw new IllegalArgumentException("Mã hex đã tồn tại"); });
        }

        c.setColorName(name);
        c.setHexCode(hex);
        c = colorRepo.save(c);

        long inUse = vehicleRepo.countByColor(c);
        return toResponse(c, inUse > 0);
    }

    @Override
    @Transactional
    public void delete(Long id, boolean force) {
        Color c = colorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy màu"));
        long using = vehicleRepo.countByColor(c);
        if (using > 0 && !force) {
            throw new IllegalStateException("Màu đang được dùng bởi " + using + " xe. Không thể xoá (dùng force=true).");
        }
        if (force && using > 0) {
            vehicleRepo.clearColorByColorId(c.getColorId()); // JPQL update dưới phần Repository
        }
        colorRepo.delete(c);
    }

    @Override
    public ColorResponse getById(Long id) {
        Color c = colorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy màu"));
        long inUse = vehicleRepo.countByColor(c);
        return toResponse(c, inUse > 0);
    }

    @Override
    public List<ColorResponse> getAll() {
        return colorRepo.findAll()
                .stream()
                .map(c -> toResponse(c, vehicleRepo.countByColor(c) > 0))
                .toList();
    }

    private ColorResponse toResponse(Color c, boolean inUse) {
        return ColorResponse.builder()
                .colorId(c.getColorId())
                .colorName(c.getColorName())
                .hexCode(c.getHexCode())
                .inUse(inUse)
                .build();
    }
}
