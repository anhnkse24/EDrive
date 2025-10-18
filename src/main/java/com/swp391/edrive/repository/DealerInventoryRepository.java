package com.swp391.edrive.repository;

import com.swp391.edrive.entity.DealerInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealerInventoryRepository extends JpaRepository<DealerInventory, Long> {
    // List toàn bộ tồn kho của 1 dealer
    List<DealerInventory> findAllByDealer_DealerId(Long dealerId);

    // Tìm 1 dòng tồn kho theo dealer + versionColor (để upsert)
    Optional<DealerInventory> findByDealer_DealerIdAndVersionColor_Id(Long dealerId, Long versionColorId);

    // Dùng trong mustGetInventory: tìm inventory theo id và dealerId (để khỏi .filter(getDealer()))
    Optional<DealerInventory> findByIdAndDealer_DealerId(Long inventoryId, Long dealerId);

    // Tổng available theo version (cộng tất cả màu)
    @Query("""
        select coalesce(sum(di.onHand - di.reserved), 0)
        from DealerInventory di
        where di.dealer.dealerId = :dealerId
          and di.versionColor.version.id = :versionId
    """)
    int sumAvailableByDealerAndVersion(Long dealerId, Long versionId);

    // Tổng available theo màu
    @Query("""
        select coalesce(sum(di.onHand - di.reserved), 0)
        from DealerInventory di
        where di.dealer.dealerId = :dealerId
          and di.versionColor.id = :versionColorId
    """)
    int sumAvailableByDealerAndVersionColor(Long dealerId, Long versionColorId);
}
