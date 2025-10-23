package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository <Customer, Long>{
    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmail(String email);
}
