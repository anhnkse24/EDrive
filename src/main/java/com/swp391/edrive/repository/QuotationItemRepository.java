package com.swp391.edrive.repository;

import com.swp391.edrive.entity.QuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuotationItemRepository extends JpaRepository<QuotationItem, Long> {
    List<QuotationItem> findByQuotation_Id(Long quotationId);

}
