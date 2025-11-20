package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByDealer_DealerId(Long dealerId);
    @Query("SELECT p FROM Promotion p WHERE p.promoId IN :promotionIds")
    Set<Promotion> findAllByIdIn(Set<Long> promotionIds);
    Optional<Promotion> findByPromoIdAndDealer_DealerId(Long promoId, Long dealerId);

}