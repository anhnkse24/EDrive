package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.PromotionRequest;
import com.swp391.edrive.dto.response.PromotionResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.Promotion;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.enums.PromoTarget;
import com.swp391.edrive.repository.*;
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
    private final DealerInventoryRepository dealerInventoryRepository;




    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
                .orElseThrow(() -> new IllegalArgumentException("Dealer không tồn tại"));

        Promotion promo = mapRequestToEntity(req, new Promotion(), dealer);
        promo.setDealer(dealer);

        return toResponse(promotionRepository.save(promo));
    }

    @Override
    public PromotionResponse updatePromotionByDealer(Long dealerId, Long promotionId, PromotionRequest req) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer không tồn tại"));

        Promotion promo = promotionRepository.findByPromoIdAndDealer_DealerId(promotionId, dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi thuộc dealer này"));

        promo = mapRequestToEntity(req, promo, dealer);

        return toResponse(promotionRepository.save(promo));
    }


    @Override
    public void deletePromotionByDealer(Long dealerId, Long promotionId) {
        Promotion promo = promotionRepository.findByPromoIdAndDealer_DealerId(promotionId, dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khuyến mãi thuộc dealer này"));
        promotionRepository.delete(promo);
    }


    private Promotion mapRequestToEntity(PromotionRequest req, Promotion promo, Dealer dealer) {

        promo.setTitle(req.getTitle());
        promo.setDescription(req.getDescription());
        promo.setDiscountType(req.getDiscountType());
        promo.setDiscountValue(req.getDiscountValue());
        promo.setStartDate(req.getStartDate());
        promo.setEndDate(req.getEndDate());
        promo.setApplicableTo(PromoTarget.VEHICLE);

        if (req.getVehicleIds() == null || req.getVehicleIds().isEmpty()) {
            throw new IllegalArgumentException("Phải cung cấp vehicleIds cho khuyến mãi.");
        }

        List<Vehicle> existingVehicles = vehicleRepository.findAllById(req.getVehicleIds());

        if (existingVehicles.size() != req.getVehicleIds().size()) {
            throw new IllegalArgumentException("Một hoặc nhiều vehicleId không tồn tại trong hệ thống.");
        }

        Set<Vehicle> vehicles = new HashSet<>(existingVehicles);

        for (Vehicle v : vehicles) {
            boolean belongsToDealer =
                    dealerInventoryRepository.existsByDealer_DealerIdAndVehicle_VehicleId(
                            dealer.getDealerId(), v.getVehicleId()
                    );


        }

        promo.setVehicles(vehicles);

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