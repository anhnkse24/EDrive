package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByDealer_DealerId(Long dealerId);

    Optional<Promotion> findByPromoIdAndDealer_DealerId(Long promoId, Long dealerId);

}