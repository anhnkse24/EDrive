package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.InventoryRequest;
import com.swp391.edrive.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {
    InventoryResponse createInventory(InventoryRequest request);
    InventoryResponse updateInventory(Long id, InventoryRequest request);
    void deleteInventory(Long id);
    InventoryResponse getInventoryById(Long id);
    List<InventoryResponse> getAllInventories();
    List<InventoryResponse> getInventoriesByDealer(Long dealerId);
}