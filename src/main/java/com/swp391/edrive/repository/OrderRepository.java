package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Order;
import com.swp391.edrive.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository <Order, String> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByDealer_DealerId(Long dealerId);
    Optional<Order> findTopByDealer_DealerIdAndPaymentImageIsNotNullOrderByOrderDateDesc(Long dealerId);

}
