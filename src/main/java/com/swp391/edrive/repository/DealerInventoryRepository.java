package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.DealerInventory;
import com.swp391.edrive.entity.Vehicle;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Registered
public interface DealerInventoryRepository extends JpaRepository<DealerInventory, Long> {
    Optional<DealerInventory> findByDealerAndVehicle(Dealer dealer, Vehicle vehicle);
    List<DealerInventory> findByDealer(Dealer dealer);
    Optional<DealerInventory> findById(Long id);

    Optional<DealerInventory> findByDealer_DealerIdAndVehicle_VehicleId(Long dealerId, Long vehicleId);
    List<DealerInventory> findByDealer_DealerId(Long dealerId);
}
