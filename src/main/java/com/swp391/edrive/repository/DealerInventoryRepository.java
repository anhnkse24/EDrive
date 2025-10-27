package com.swp391.edrive.repository;

import com.swp391.edrive.entity.DealerInventory;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Registered
public interface DealerInventoryRepository extends JpaRepository<DealerInventory, Long> {
    Optional<DealerInventory> findByDealer_DealerIdAndVehicle_VehicleId(Long dealerId, Long vehicleId);
}
