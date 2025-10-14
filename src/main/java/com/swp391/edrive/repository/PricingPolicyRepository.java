package com.swp391.edrive.repository;

import com.swp391.edrive.entity.PricingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PricingPolicyRepository extends JpaRepository<PricingPolicyRepository, Long> {
    // Ưu tiên theo màu
    List<PricingPolicy> findByDealer_IdAndVersionColor_IdAndStatus(Long dealerId, Long versionColorId, String status);

    // Theo phiên bản (khi không có rule theo màu)
    List<PricingPolicy> findByDealer_IdAndVersion_IdAndStatus(Long dealerId, Long versionId, String status);

    // Lọc theo hiệu lực (nếu bạn muốn)
    List<PricingPolicy> findByDealer_IdAndVersionColor_IdAndStatusAndValidFromLessThanEqualAndValidToGreaterThanEqual(
            Long dealerId, Long versionColorId, String status, LocalDate from, LocalDate to);
}
