package com.swp391.edrive.repository;

import com.swp391.edrive.entity.ManufacturerInventory;
import com.swp391.edrive.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ManufacturerInventoryRepository extends JpaRepository<ManufacturerInventory, Long> {
    Optional<ManufacturerInventory> findByVehicle_VehicleId(Long vehicleId);
}
