package com.swp391.edrive.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalServicesRequest {
    private Boolean hasTintFilm;           // Phim cách nhiệt cao cấp
    private Boolean hasWallboxCharger;     // Bộ sạc Wallbox 7kW
    private Boolean hasWarrantyExtension;  // Gói bảo hành mở rộng 2 năm
    private Boolean hasPPF;                // PPF toàn xe
    private Boolean hasCeramicCoating;     // Phủ Ceramic 9H
    private Boolean has360Camera;          // Camera hành trình 360
}

