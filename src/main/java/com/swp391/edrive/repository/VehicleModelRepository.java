package com.swp391.edrive.repository;

import com.swp391.edrive.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleModelRepository extends JpaRepository <VehicleModel, Long>{
    Optional<VehicleModel> findByModelName(String modelName);
    boolean existsByModelName(String modelName);
}
