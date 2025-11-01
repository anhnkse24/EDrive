package com.swp391.edrive.repository;

import com.swp391.edrive.entity.ManufacturerInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManufacturerInventoryRepository extends JpaRepository<ManufacturerInventory, Long> {
    Optional<ManufacturerInventory> findByVehicle_VehicleId(Long vehicleId);

}
