package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.response.VehicleColorOptionResponse;
import com.swp391.edrive.repository.VehicleVersionRepository;
import com.swp391.edrive.service.VehicleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleQueryServiceImpl implements VehicleQueryService {
    private final VehicleVersionRepository versionRepo;
    @Override
    public List<VehicleColorOptionResponse> searchByColor(String color) {
        return versionRepo.searchVersionColorsByColor(color.trim());
    }
}
