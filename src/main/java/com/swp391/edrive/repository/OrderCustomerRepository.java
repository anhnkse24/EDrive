package com.swp391.edrive.repository;

import com.swp391.edrive.entity.OrderCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderCustomerRepository extends JpaRepository<OrderCustomer, Long> {
    List<OrderCustomer> findByCustomer_CustomerId(Long customerId);
}
