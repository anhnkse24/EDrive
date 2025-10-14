package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Quotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface QuotationRepository {
    Page<Quotation> findByDealer_Id(Long dealerId, Pageable pageable);
    Page<Quotation> findByCustomer_Id(Long customerId, Pageable pageable);
    Optional<Quotation> findByQuoteCode(String quoteCode);
    boolean existsByQuoteCode(String quoteCode);
}
