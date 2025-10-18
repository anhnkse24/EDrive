package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.VehicleModelUpsertRequest;
import com.swp391.edrive.dto.response.VehicleModelResponse;

import java.util.List;

public interface VehicleModelService {
    VehicleModelResponse create(VehicleModelUpsertRequest req);
    VehicleModelResponse get(Long id);
    List<VehicleModelResponse> list();
    VehicleModelResponse update(Long id, VehicleModelUpsertRequest req);
    void delete(Long id);
}
