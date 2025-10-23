package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {
    Optional<Dealer> findByDealerName(String dealerName);
    boolean existsByDealerName(String dealerName);
    boolean existsByDealerCode(String dealerCode);
}
