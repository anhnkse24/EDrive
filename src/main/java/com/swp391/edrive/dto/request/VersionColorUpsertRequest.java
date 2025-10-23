package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VersionColorUpsertRequest {
    @NotBlank
    private String colorName;

    @NotBlank private String colorCode;
    private String imageUrl;

    @NotNull
    private Boolean active;

    private BigDecimal priceDelta;

    private BigDecimal priceOverride;
}
