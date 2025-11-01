package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.ManufacturerInventoryRequest;
import com.swp391.edrive.dto.response.ManufacturerInventoryResponse;
import com.swp391.edrive.dto.response.ManufacturerInventorySummaryResponse;

import java.util.List;

public interface ManufacturerInventoryService {
    List<ManufacturerInventoryResponse> getAll();
    ManufacturerInventoryResponse getById(Long id);
    ManufacturerInventoryResponse create(ManufacturerInventoryRequest request);
    ManufacturerInventoryResponse update(Long id, ManufacturerInventoryRequest request);
    void delete(Long id);

    List<ManufacturerInventorySummaryResponse> getGroupedByManufacturer();

}
