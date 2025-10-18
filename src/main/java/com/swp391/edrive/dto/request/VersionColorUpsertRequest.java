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

    @NotBlank private String colorCode;    // unique trong 1 version
    private String imageUrl;

    @NotNull
    private Boolean active;      // bật/tắt bán

    private BigDecimal priceDelta;         // có thể âm/dương/null

    private BigDecimal priceOverride;      // > 0 nếu có
}
