package com.swp391.edrive.repository;

import com.swp391.edrive.entity.VehicleVersion;
import com.swp391.edrive.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VehicleVersionRepository extends JpaRepository <VehicleVersion, Long> {
    Page<VehicleVersion> findByStatus(VehicleStatus status, Pageable pageable);

    Page<VehicleVersion> findByManufactureYear(Integer manufactureYear, Pageable pageable);

    Page<VehicleVersion> findByManufactureYearBetween(Integer fromYear, Integer toYear, Pageable pageable);

    // lọc theo màu: join sang VersionColor (distinct để tránh trùng)
    Page<VehicleVersion> findDistinctByColors_ColorNameContainingIgnoreCase(String color, Pageable pageable);

    // lọc theo giá base (phiên bản)
    Page<VehicleVersion> findByBasePriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);
    Page<VehicleVersion> findByBasePriceGreaterThanEqual(BigDecimal min, Pageable pageable);
    Page<VehicleVersion> findByBasePriceLessThanEqual(BigDecimal max, Pageable pageable);
}
