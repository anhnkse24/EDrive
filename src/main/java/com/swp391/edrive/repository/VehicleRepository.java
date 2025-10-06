package com.swp391.edrive.repository;


import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Page<Vehicle> findByStatus(VehicleStatus status, Pageable pageable);

    Page<Vehicle> findByColorIgnoreCaseContaining(String color, Pageable pageable);

    Page<Vehicle> findByManufactureYear(Integer manufactureYear, Pageable pageable);

    Page<Vehicle> findByManufactureYearBetween(Integer fromYear, Integer toYear, Pageable pageable);

    Page<Vehicle> findByPriceRetailBetween(Double minPrice, Double maxPrice, Pageable pageable);

    Page<Vehicle> findByPriceRetailGreaterThanEqual(Double minPrice, Pageable pageable);

    Page<Vehicle> findByPriceRetailLessThanEqual(Double maxPrice, Pageable pageable);
}
