package com.swp391.edrive.service;

import com.swp391.edrive.dto.response.FeedbackResponse;
import org.springframework.data.domain.Page;

public interface FeedbackService {
    Page<FeedbackResponse> getAll(int page, int size);
    FeedbackResponse getById(Long id);
    Page<FeedbackResponse> getByCustomerId(Long customerId, int page, int size);
    Page<FeedbackResponse> getByDealerId(Long dealerId, int page, int size);
    void deleteById(Long id);
}
