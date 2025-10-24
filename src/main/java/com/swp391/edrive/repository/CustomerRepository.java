package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository <Customer, Long>{
    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmail(String email);
    List<Customer> findByDealer_DealerId(Long dealerId);
}
