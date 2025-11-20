package com.swp391.edrive.service;

import com.swp391.edrive.dto.response.ColorBriefResponse;
import com.swp391.edrive.dto.response.VehicleColorOptionResponse;
import com.swp391.edrive.dto.response.VehicleResponse;
import com.swp391.edrive.entity.VehicleVersion;
import com.swp391.edrive.entity.VersionColor;
import com.swp391.edrive.enums.VehicleStatus;
import com.swp391.edrive.repository.VehicleVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


public interface VehicleQueryService {
    List<VehicleColorOptionResponse> searchByColor(String color);
}
