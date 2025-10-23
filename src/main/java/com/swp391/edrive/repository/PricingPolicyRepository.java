    package com.swp391.edrive.repository;

    import com.swp391.edrive.entity.PricingPolicy;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.time.LocalDate;
    import java.util.List;

    @Repository
    public interface PricingPolicyRepository extends JpaRepository<PricingPolicy, Long> {
        List<PricingPolicy> findByDealer_DealerIdAndVersionColor_IdAndStatus(
                Long dealerId, Long versionColorId, String status);

        List<PricingPolicy> findByDealer_DealerIdAndVersion_IdAndStatus(
                Long dealerId, Long versionId, String status);

        List<PricingPolicy> findByDealer_DealerIdAndVersionColor_IdAndStatusAndValidFromLessThanEqualAndValidToGreaterThanEqual(
                Long dealerId, Long versionColorId, String status, LocalDate from, LocalDate to);
    }
