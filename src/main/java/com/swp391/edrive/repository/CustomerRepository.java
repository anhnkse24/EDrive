package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerIdAndDealer_DealerId(Long customerId, Long dealerId);
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByFullName(String fullName);
    List<Customer> findByDealer_DealerId(Long dealerId);
    Optional<Customer> findByFullNameAndPhone(String fullName, String phone);

}