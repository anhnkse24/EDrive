package com.swp391.edrive.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppliedPromotionResponse {
    private String title;
    private String description;
    private String discountType;  // PERCENTAGE hoặc FIXED_AMOUNT
    private Double discountValue;
    private BigDecimal discountAmount;  // Số tiền giảm thực tế
    private String applicableTo;  // VEHICLE hoặc CUSTOMER
}

