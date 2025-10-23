//package com.swp391.edrive.repository;
//
//
//import com.swp391.edrive.entity.VehicleModel;
//import com.swp391.edrive.enums.VehicleStatus;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface VehicleRepository extends JpaRepository<VehicleModel, Long> {
//    Page<VehicleModel> findByStatus(VehicleStatus status, Pageable pageable);
//
//    Page<VehicleModel> findByColorIgnoreCaseContaining(String color, Pageable pageable);
//
//    Page<VehicleModel> findByManufactureYear(Integer manufactureYear, Pageable pageable);
//
//    Page<VehicleModel> findByManufactureYearBetween(Integer fromYear, Integer toYear, Pageable pageable);
//
//    Page<VehicleModel> findByPriceRetailBetween(Double minPrice, Double maxPrice, Pageable pageable);
//
//    Page<VehicleModel> findByPriceRetailGreaterThanEqual(Double minPrice, Pageable pageable);
//
//    Page<VehicleModel> findByPriceRetailLessThanEqual(Double maxPrice, Pageable pageable);
//}
