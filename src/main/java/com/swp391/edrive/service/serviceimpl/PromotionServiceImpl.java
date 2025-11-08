package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.PromotionRequest;
import com.swp391.edrive.dto.response.PromotionResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.Promotion;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.PromotionRepository;
import com.swp391.edrive.repository.VehicleRepository;
import com.swp391.edrive.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final VehicleRepository vehicleRepository;
    private final DealerRepository dealerRepository;


    @Override
    public PromotionResponse createPromotion(PromotionRequest req) {
        Promotion promo = mapRequestToEntity(req, new Promotion());
        Promotion saved = promotionRepository.save(promo);
        return toResponse(saved);
    }

    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest req) {
        Promotion promo = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi với ID = " + id));

        promo = mapRequestToEntity(req, promo);
        Promotion updated = promotionRepository.save(promo);
        return toResponse(updated);
    }

    @Override
    public PromotionResponse getPromotionById(Long id) {
        Promotion promo = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found"));
        return toResponse(promo);
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new IllegalArgumentException("Promotion not found");
        }
        promotionRepository.deleteById(id);
    }


    @Override
    public List<PromotionResponse> getPromotionsByDealerId(Long dealerId) {
        return promotionRepository.findByDealer_DealerId(dealerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse getPromotionByIdAndDealerId(Long promotionId, Long dealerId) {
        Promotion promo = promotionRepository.findByPromoIdAndDealer_DealerId(promotionId, dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi của dealer này"));
        return toResponse(promo);
    }

    @Override
    public PromotionResponse createPromotionByDealer(Long dealerId, PromotionRequest req) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer không tồn tại với ID = " + dealerId));

        Promotion promo = mapRequestToEntity(req, new Promotion());
        promo.setDealer(dealer);

        Promotion saved = promotionRepository.save(promo);
        return toResponse(saved);
    }

    @Override
    public PromotionResponse updatePromotionByDealer(Long dealerId, Long promotionId, PromotionRequest req) {
        Promotion promo = promotionRepository.findByPromoIdAndDealer_DealerId(promotionId, dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi thuộc dealer này"));

        promo = mapRequestToEntity(req, promo);
        Promotion updated = promotionRepository.save(promo);
        return toResponse(updated);
    }

    @Override
    public void deletePromotionByDealer(Long dealerId, Long promotionId) {
        Promotion promo = promotionRepository.findByPromoIdAndDealer_DealerId(promotionId, dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi thuộc dealer này"));
        promotionRepository.delete(promo);
    }


    private Promotion mapRequestToEntity(PromotionRequest req, Promotion promo) {
        promo.setTitle(req.getTitle());
        promo.setDescription(req.getDescription());
        promo.setDiscountType(req.getDiscountType());
        promo.setDiscountValue(req.getDiscountValue());
        promo.setStartDate(req.getStartDate());
        promo.setEndDate(req.getEndDate());
        promo.setApplicableTo(req.getApplicableTo());

        if (req.getVehicleIds() != null && !req.getVehicleIds().isEmpty()) {
            Set<Vehicle> vehicles = new HashSet<>(vehicleRepository.findAllById(req.getVehicleIds()));
            promo.setVehicles(vehicles);
        } else {
            promo.setVehicles(new HashSet<>());
        }
        return promo;
    }

    private PromotionResponse toResponse(Promotion promo) {
        return PromotionResponse.builder()
                .promoId(promo.getPromoId())
                .title(promo.getTitle())
                .description(promo.getDescription())
                .discountType(promo.getDiscountType())
                .discountValue(promo.getDiscountValue())
                .startDate(promo.getStartDate())
                .endDate(promo.getEndDate())
                .applicableTo(promo.getApplicableTo())
                .dealerId(promo.getDealer() != null ? promo.getDealer().getDealerId() : null)
                .vehicleIds(promo.getVehicles()
                        .stream()
                        .map(Vehicle::getVehicleId)
                        .collect(Collectors.toList()))
                .build();
    }
}
