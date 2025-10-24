package com.swp391.edrive.repository;

import com.swp391.edrive.entity.TestDrive;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestDriveRepository extends JpaRepository<TestDrive, Long> {
    List<TestDrive> findByDealer(Dealer dealer);
    List<TestDrive> findByCustomer(Customer customer);
    List<TestDrive> findByDealer_DealerId(Long dealerId);

}
