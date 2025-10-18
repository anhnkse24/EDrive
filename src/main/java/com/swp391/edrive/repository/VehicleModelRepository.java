package com.swp391.edrive.repository;

import com.swp391.edrive.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleModelRepository extends JpaRepository <VehicleModel, Long>{
    boolean existsByModelNameIgnoreCase(String modelName);
    Optional<VehicleModel> findByModelNameIgnoreCase(String modelName);
    boolean existsByModelNameIgnoreCaseAndIdNot(String modelName, Long id);
}
