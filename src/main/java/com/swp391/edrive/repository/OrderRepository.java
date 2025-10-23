package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository <Order, Long>{
    boolean existsByQuotation_Id(Long quotationId);

}
