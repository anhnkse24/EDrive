package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByDealer_DealerId(Long dealerId);
}
