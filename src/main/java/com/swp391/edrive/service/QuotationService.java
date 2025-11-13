package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.QuotationRequest;
import com.swp391.edrive.dto.response.QuotationResponse;

public interface QuotationService {
        QuotationResponse createQuotation(QuotationRequest quotationRequest);
}
