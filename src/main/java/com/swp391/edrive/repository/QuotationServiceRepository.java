package com.swp391.edrive.repository;

import com.swp391.edrive.entity.QuotationServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationServiceRepository extends JpaRepository<QuotationServices, Long> {

    // Tìm các dịch vụ đã chọn theo Quotation ID
    List<QuotationServices> findByQuotationQuotationId(Long quotationId);
}

