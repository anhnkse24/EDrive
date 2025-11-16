package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Contract;
import com.swp391.edrive.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByDealer_DealerId(Long dealerId);
    List<Contract> findByStatus(ContractStatus status);
    boolean existsByOrder_OrderId(String orderId);
}
