package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByDealer_DealerId(Long dealerId);

}