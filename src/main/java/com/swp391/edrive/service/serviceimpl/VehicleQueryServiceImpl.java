package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.response.ColorBriefResponse;
import com.swp391.edrive.dto.response.VehicleColorOptionResponse;
import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.entity.VehicleVersion;
import com.swp391.edrive.entity.VersionColor;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.repository.VehicleVersionRepository;
import com.swp391.edrive.service.VehicleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleQueryServiceImpl implements VehicleQueryService {
    private final VehicleVersionRepository versionRepo;
    @Override
    public List<VehicleColorOptionResponse> searchByColor(String color) {
        return versionRepo.searchVersionColorsByColor(color.trim());
    }
}
