package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Contract;
import com.swp391.edrive.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository <Contract, Long> {
    Optional<Contract> findByQuotation_Id(Long quotationId);
    List<Contract> findByDealer_DealerId(Long dealerId);
    List<Contract> findByStatus(ContractStatus status);
    boolean existsByQuotation_Id(Long quotationId);
}
