package com.swp391.edrive.repository;

import com.swp391.edrive.entity.DiscountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicy, Long> {

    @Query("SELECT dp FROM DiscountPolicy dp " +
           "WHERE :quantity >= dp.minQuantity " +
           "AND :quantity <= dp.maxQuantity " +
           "AND dp.isActive = true " +
           "ORDER BY dp.discountRate DESC")
    Optional<DiscountPolicy> findByQuantityRange(@Param("quantity") Integer quantity);
}

