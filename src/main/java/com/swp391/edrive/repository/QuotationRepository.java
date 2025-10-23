package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Quotation;
import com.swp391.edrive.enums.QuotationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    // nếu cần paging theo Dealer/Customer, đặt đúng tên field khoá của bạn
    List<Quotation> findByDealer_DealerId(Long dealerId);
    List<Quotation> findByCustomer_CustomerId(Long customerId);

    List<Quotation> findByStatus(QuotationStatus status);


}
