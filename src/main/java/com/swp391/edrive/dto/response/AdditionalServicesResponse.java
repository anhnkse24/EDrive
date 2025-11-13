package com.swp391.edrive.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalServicesResponse {
    private Boolean hasTintFilm;
    private BigDecimal tintFilmPrice;

    private Boolean hasWallboxCharger;
    private BigDecimal wallboxChargerPrice;

    private Boolean hasWarrantyExtension;
    private BigDecimal warrantyExtensionPrice;

    private Boolean hasPPF;
    private BigDecimal ppfPrice;

    private Boolean hasCeramicCoating;
    private BigDecimal ceramicCoatingPrice;

    private Boolean has360Camera;
    private BigDecimal camera360Price;

    private BigDecimal totalServicesPrice; // Tổng giá dịch vụ bổ sung
}

