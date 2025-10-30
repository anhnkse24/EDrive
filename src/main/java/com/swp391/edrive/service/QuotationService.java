package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.QuotationCreateRequest;
import com.swp391.edrive.dto.response.QuotationResponse;

import java.util.List;

public interface QuotationService {
    QuotationResponse previewQuotation(QuotationCreateRequest req);
    QuotationResponse createQuotation(QuotationCreateRequest req);
    QuotationResponse getQuotation(Long id);
    List<QuotationResponse> getAllQuotations();

}
