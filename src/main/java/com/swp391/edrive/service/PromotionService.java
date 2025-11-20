package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.PromotionRequest;
import com.swp391.edrive.dto.response.PromotionResponse;
import java.util.List;

public interface PromotionService {
    List<PromotionResponse> getAllPromotions();
    List<PromotionResponse> getPromotionsByDealerId(Long dealerId);
    PromotionResponse getPromotionByIdAndDealerId(Long promotionId, Long dealerId);
    PromotionResponse createPromotionByDealer(Long dealerId, PromotionRequest req);
    PromotionResponse updatePromotionByDealer(Long dealerId, Long promotionId, PromotionRequest req);
    void deletePromotionByDealer(Long dealerId, Long promotionId);
}