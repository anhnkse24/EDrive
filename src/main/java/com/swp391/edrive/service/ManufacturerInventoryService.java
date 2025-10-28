package com.swp391.edrive.service;

import com.swp391.edrive.dto.response.InventoryResponse;

import java.util.List;

public interface ManufacturerInventoryService {
    List<InventoryResponse> getAllInventories();
    InventoryResponse getById(Long id);
    InventoryResponse getByVehicleId(Long vehicleId);
    List<InventoryResponse> getByManufacturerId(Long manufacturerId);
    void deleteInventory(Long id);
}
