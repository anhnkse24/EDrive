package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.DiscountType;
import com.swp391.edrive.enums.PromoTarget;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionResponse {
    private Long promoId;
    private String title;
    private String description;
    private DiscountType discountType;
    private Double discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private PromoTarget applicableTo;

    private Long dealerId;
    private List<Long> vehicleIds;
    private List<Long> customerIds;
}
