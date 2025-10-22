package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.PromotionRequest;
import com.swp391.edrive.dto.response.PromotionResponse;
import com.swp391.edrive.entity.Promotion;
import com.swp391.edrive.entity.Vehicle;
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

    @Override
    public PromotionResponse createPromotion(PromotionRequest req) {
        Promotion promo = new Promotion();
        promo.setTitle(req.getTitle());
        promo.setDescription(req.getDescription());
        promo.setDiscountType(req.getDiscountType());
        promo.setDiscountValue(req.getDiscountValue());
        promo.setStartDate(req.getStartDate());
        promo.setEndDate(req.getEndDate());
        promo.setApplicableTo(req.getApplicableTo());

        // Gắn nhiều vehicle vào promotion
        if (req.getVehicleIds() != null && !req.getVehicleIds().isEmpty()) {
            Set<Vehicle> vehicles = new HashSet<>(vehicleRepository.findAllById(req.getVehicleIds()));
            promo.setVehicles(vehicles);
        }

        Promotion saved = promotionRepository.save(promo);

        // Trả về response
        return PromotionResponse.builder()
                .promoId(saved.getPromoId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .discountType(saved.getDiscountType())
                .discountValue(saved.getDiscountValue())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .applicableTo(saved.getApplicableTo())
                .vehicleIds(saved.getVehicles()
                        .stream()
                        .map(Vehicle::getVehicleId)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public PromotionResponse getPromotionById(Long id) {
        Promotion promo = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found"));

        return PromotionResponse.builder()
                .promoId(promo.getPromoId())
                .title(promo.getTitle())
                .description(promo.getDescription())
                .discountType(promo.getDiscountType())
                .discountValue(promo.getDiscountValue())
                .startDate(promo.getStartDate())
                .endDate(promo.getEndDate())
                .applicableTo(promo.getApplicableTo())
                .vehicleIds(promo.getVehicles()
                        .stream()
                        .map(Vehicle::getVehicleId)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(promo -> PromotionResponse.builder()
                        .promoId(promo.getPromoId())
                        .title(promo.getTitle())
                        .description(promo.getDescription())
                        .discountType(promo.getDiscountType())
                        .discountValue(promo.getDiscountValue())
                        .startDate(promo.getStartDate())
                        .endDate(promo.getEndDate())
                        .applicableTo(promo.getApplicableTo())
                        .vehicleIds(promo.getVehicles()
                                .stream()
                                .map(Vehicle::getVehicleId)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new IllegalArgumentException("Promotion not found");
        }
        promotionRepository.deleteById(id);
    }
}
