package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.QuotationRequest;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.entity.User;

import java.util.List;
import java.util.Optional;

public interface QuotationService {
    QuotationResponse createQuotation(QuotationRequest quotationRequest, User createdByUser);
    // Cập nhật trạng thái quotation (ACCEPTED/REJECTED)
    QuotationResponse updateQuotationStatus(Long quotationId, String status, String rejectionReason);

    List<QuotationResponse> getAllQuotations();
    Optional<QuotationResponse> getQuotationById(Long quotationId);
}
