package com.swp391.edrive.service;

import com.swp391.edrive.dto.response.DealerInventoryDTO;


import java.util.List;

public interface DealerInventoryService {
    DealerInventoryDTO updateDealerInventory(Long dealerId, Long vehicleId, int quantity);
    List<DealerInventoryDTO> getDealerInventoryByDealerId(Long dealerId);
}
