package com.swp391.edrive.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Embeddable  // Đánh dấu lớp này có thể nhúng vào entity khác
public class AdditionalServices {

    private Boolean hasTintFilm;  // Phim cách nhiệt cao cấp
    private BigDecimal tintFilmPrice;  // Giá trị của dịch vụ phim cách nhiệt

    private Boolean hasWallboxCharger; // Bộ sạc Wallbox 7kW
    private BigDecimal wallboxChargerPrice; // Giá trị của dịch vụ bộ sạc Wallbox

    private Boolean hasWarrantyExtension; // Gói bảo hành mở rộng 2 năm
    private BigDecimal warrantyExtensionPrice; // Giá trị của dịch vụ bảo hành mở rộng

    private Boolean hasPPF; // PPF toàn xe
    private BigDecimal ppfPrice; // Giá trị của dịch vụ PPF toàn xe

    private Boolean hasCeramicCoating; // Phủ Ceramic 9H
    private BigDecimal ceramicCoatingPrice; // Giá trị của dịch vụ phủ Ceramic 9H

    private Boolean has360Camera; // Camera hành trình 360
    private BigDecimal camera360Price; // Giá trị của dịch vụ Camera hành trình 360
}
