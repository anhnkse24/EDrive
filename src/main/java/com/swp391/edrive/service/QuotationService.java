package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.QuotationRequest;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.enums.CustomerQuotationStatus;

import java.util.List;
import java.util.Optional;

public interface QuotationService {
    QuotationResponse createQuotation(QuotationRequest quotationRequest, User createdByUser);
    // Cập nhật trạng thái quotation (ACCEPTED/REJECTED)
    QuotationResponse updateQuotationStatus(Long quotationId, String status, String rejectionReason);

    QuotationResponse updateCustomerQuotationStatus(Long quotationId, CustomerQuotationStatus newStatus);
    List<QuotationResponse> getAllQuotations();
    Optional<QuotationResponse> getQuotationById(Long quotationId);

    // Gửi email báo giá cho khách hàng (chỉ áp dụng cho báo giá đã được duyệt)
    void sendQuotationEmailToCustomer(Long quotationId);
}
