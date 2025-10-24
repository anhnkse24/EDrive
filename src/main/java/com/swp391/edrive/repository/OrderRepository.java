package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Order;
import com.swp391.edrive.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository <Order, Long> {
    // Lấy danh sách Order theo đại lý
    List<Order> findByDealer_DealerId(Long dealerId);

    // Một số truy vấn tiện dụng theo đại lý
    List<Order> findTop20ByDealer_DealerIdOrderByOrderDateDesc(Long dealerId);

    long countByDealer_DealerIdAndStatus(Long dealerId, OrderStatus status);

}
