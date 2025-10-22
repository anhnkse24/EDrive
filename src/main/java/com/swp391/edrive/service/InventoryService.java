package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.DealerInventoryAdjustRequest;
import com.swp391.edrive.dto.request.DealerInventoryUpdateRequest;
import com.swp391.edrive.dto.request.DealerInventoryUpsertRequest;
import com.swp391.edrive.dto.response.DealerInventoryResponse;

import java.util.List;

public interface InventoryService {
    // READ
    List<DealerInventoryResponse> list(Long dealerId, Long versionId, Long versionColorId, boolean onlyAvailable);
    int getDemoCapacityByVersion(Long dealerId, Long versionId);
    int getDemoCapacityByVersionColor(Long dealerId, Long versionColorId);

    // WRITE
    DealerInventoryResponse upsert(Long dealerId, DealerInventoryUpsertRequest req);
    DealerInventoryResponse update(Long dealerId, Long inventoryId, DealerInventoryUpdateRequest req);
    DealerInventoryResponse adjust(Long dealerId, Long inventoryId, DealerInventoryAdjustRequest req);
    void delete(Long dealerId, Long inventoryId);
    void reserveDemoVehicle(Long dealerId, Long versionId, Long versionColorId);
    void releaseDemoVehicle(Long dealerId, Long versionId, Long versionColorId);
}
