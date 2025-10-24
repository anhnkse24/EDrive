package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.PromotionRequest;
import com.swp391.edrive.dto.response.PromotionResponse;
import java.util.List;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest request);
    PromotionResponse getPromotionById(Long id);
    List<PromotionResponse> getAllPromotions();
    PromotionResponse updatePromotion(Long id, PromotionRequest request);
    void deletePromotion(Long id);
    List<PromotionResponse> getPromotionsByDealerId(Long dealerId);

}