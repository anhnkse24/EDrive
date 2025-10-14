package com.swp391.edrive.repository;

import com.swp391.edrive.entity.DealerInventory;

import java.util.List;
import java.util.Optional;

public interface DealerInventoryRepository {
    // Tồn kho của 1 đại lý
    List<DealerInventory> findByDealer_Id(Long dealerId);

    // Tồn kho 1 màu (versionColor) tại 1 đại lý
    Optional<DealerInventory> findByDealer_IdAndVersionColor_Id(Long dealerId, Long versionColorId);

    boolean existsByDealer_IdAndVersionColor_Id(Long dealerId, Long versionColorId);
}
