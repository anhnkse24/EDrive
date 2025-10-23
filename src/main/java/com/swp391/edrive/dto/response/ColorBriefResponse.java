package com.swp391.edrive.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorBriefResponse {
    private Long colorId;
    private String colorName;
    private String colorCode;
    private String imageUrl;
    private Boolean active;
    private BigDecimal priceDelta;
    private BigDecimal priceOverride;
    private BigDecimal retailPrice;
}
