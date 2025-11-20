package com.swp391.edrive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountPolicyResponse {
    private Long id;
    private Integer minQuantity;
    private Integer maxQuantity;
    private BigDecimal discountRate;
    private Boolean isActive;
    private String description;
}

