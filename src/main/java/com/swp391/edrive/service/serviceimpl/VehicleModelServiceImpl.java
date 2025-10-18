package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.VehicleModelUpsertRequest;
import com.swp391.edrive.dto.response.VehicleModelResponse;
import com.swp391.edrive.entity.VehicleModel;
import com.swp391.edrive.repository.VehicleModelRepository;
import com.swp391.edrive.service.VehicleModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleModelServiceImpl implements VehicleModelService {
    private final VehicleModelRepository modelRepo;

    @Override
    public VehicleModelResponse create(VehicleModelUpsertRequest req) {
        // Unique theo modelName
        if (modelRepo.existsByModelNameIgnoreCase(req.getModelName().trim())) {
            throw new IllegalArgumentException("Model name already exists: " + req.getModelName());
        }
        VehicleModel m = VehicleModel.builder()
                .modelName(req.getModelName().trim())
                .description(req.getDescription())
                .imageUrl(req.getImageUrl())
                .build();
        m = modelRepo.save(m);
        return toDto(m);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleModelResponse get(Long id) {
        VehicleModel m = modelRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle model not found with id=" + id));
        return toDto(m);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleModelResponse> list() {
        return modelRepo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public VehicleModelResponse update(Long id, VehicleModelUpsertRequest req) {
        VehicleModel m = modelRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle model not found with id=" + id));
        // nếu đổi tên, kiểm tra unique
        String newName = req.getModelName().trim();
        if (!m.getModelName().equalsIgnoreCase(newName)
                && modelRepo.existsByModelNameIgnoreCase(newName)) {
            throw new IllegalArgumentException("Model name already exists: " + newName);
        }
        m.setModelName(newName);
        m.setDescription(req.getDescription());
        m.setImageUrl(req.getImageUrl());
        return toDto(m);
    }

    @Override
    public void delete(Long id) {
        VehicleModel m = modelRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle model not found with id=" + id));
        // NGHIỆP VỤ: chặn xóa nếu còn version để tránh xóa dây chuyền
        if (m.getVersions() != null && !m.getVersions().isEmpty()) {
            throw new IllegalStateException("Cannot delete model that still has versions");
        }
        modelRepo.delete(m);
    }

    private VehicleModelResponse toDto(VehicleModel m) {
        return VehicleModelResponse.builder()
                .id(m.getId())
                .modelName(m.getModelName())
                .description(m.getDescription())
                .imageUrl(m.getImageUrl())
                .versionCount(m.getVersions() == null ? 0 : m.getVersions().size())
                .build();
    }

}
