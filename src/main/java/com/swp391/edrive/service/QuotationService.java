package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.CreateQuotationRequest;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.enums.QuotationStatus;
import com.swp391.edrive.repository.CustomerRepository;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.QuotationRepository;
import com.swp391.edrive.repository.VersionColorRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuotationService {
    QuotationResponse createDraft(CreateQuotationRequest req);
    QuotationResponse send(Long quotationId);
    QuotationResponse approve(Long quotationId);
    QuotationResponse cancel(Long quotationId, String reason);
    QuotationResponse get(Long quotationId);
    List<QuotationResponse> getAll();
    QuotationResponse getById(Long id);
    List<QuotationResponse> getByDealer(Long dealerId);
    List<QuotationResponse> getByStatus(QuotationStatus status);
}
