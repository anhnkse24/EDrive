package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.ManufacturerInventoryRequest;
import com.swp391.edrive.dto.response.InventoryResponse;

import java.util.List;

public interface ManufacturerInventoryService {
    List<InventoryResponse> getAllInventories();
    InventoryResponse getById(Long id);
    InventoryResponse getByVehicleId(Long vehicleId);
    List<InventoryResponse> getByManufacturerId(Long manufacturerId);
    InventoryResponse createInventory(ManufacturerInventoryRequest request);
    InventoryResponse updateInventory(Long id, ManufacturerInventoryRequest request);
    void deleteInventory(Long id);
}
