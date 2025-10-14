package com.swp391.edrive.repository;

import com.swp391.edrive.dto.response.VehicleColorOptionResponse;
import com.swp391.edrive.entity.VehicleVersion;
import com.swp391.edrive.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VehicleVersionRepository extends JpaRepository<VehicleVersion, Long> {
    Page<VehicleVersion> findByStatus(VehicleStatus status, Pageable pageable);

    Page<VehicleVersion> findDistinctByColors_ColorNameContainingIgnoreCase(String color, Pageable pageable);

    Page<VehicleVersion> findByManufactureYear(Integer year, Pageable pageable);

    Page<VehicleVersion> findByManufactureYearBetween(Integer fromYear, Integer toYear, Pageable pageable);

    Page<VehicleVersion> findByBasePriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

    Page<VehicleVersion> findByBasePriceGreaterThanEqual(BigDecimal min, Pageable pageable);

    Page<VehicleVersion> findByBasePriceLessThanEqual(BigDecimal max, Pageable pageable);

    // ==== Dành cho VehicleQueryServiceImpl (trả về kèm danh sách màu) ====
    @Query("""
            select distinct v
            from VehicleVersion v
            join fetch v.model m
            left join fetch v.colors vc
            where (:status is null or v.status = :status)
              and (vc is null or vc.isActive = true)
            """)
    List<VehicleVersion> findAllWithActiveColors(@Param("status") VehicleStatus status);

    @Query("""
            select distinct v
            from VehicleVersion v
            join fetch v.model m
            join fetch v.colors vc
            where (:status is null or v.status = :status)
              and vc.isActive = true
              and (:colorCode is null or vc.colorCode = :colorCode)
            """)
    List<VehicleVersion> findAllByColorCode(@Param("status") VehicleStatus status,
                                            @Param("colorCode") String colorCode);

    @Query("""
            select new com.swp391.edrive.dto.response.VehicleColorOptionResponse(
              v.id, m.id, m.modelName, v.versionName,
              c.id, c.colorName, c.colorCode, c.imageUrl,
              coalesce(c.priceOverride, v.basePrice + coalesce(c.priceDelta, (v.basePrice - v.basePrice))),
              v.status, v.manufactureYear
            )
            from VehicleVersion v
            join v.model m
            join v.colors c
            where c.isActive = true
              and lower(c.colorName) like lower(concat('%', :color, '%'))
            """)
    List<VehicleColorOptionResponse> searchVersionColorsByColor(@Param("color") String color);
}
