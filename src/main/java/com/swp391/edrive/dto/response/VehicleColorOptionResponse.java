package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.VehicleStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class VehicleColorOptionResponse {
    private Long versionId;
    private Long modelId;
    private String modelName;
    private String versionName;

    private Long colorId;
    private String colorName;
    private String colorCode;
    private String imageUrl;

    private BigDecimal retailPrice;
    private VehicleStatus status;
    private Integer manufactureYear;

    public VehicleColorOptionResponse(
            Long versionId, Long modelId, String modelName, String versionName,
            Long colorId, String colorName, String colorCode, String imageUrl,
            BigDecimal retailPrice, VehicleStatus status, Integer manufactureYear
    ) {
        this.versionId = versionId;
        this.modelId = modelId;
        this.modelName = modelName;
        this.versionName = versionName;
        this.colorId = colorId;
        this.colorName = colorName;
        this.colorCode = colorCode;
        this.imageUrl = imageUrl;
        this.retailPrice = retailPrice;
        this.status = status;
        this.manufactureYear = manufactureYear;
    }
}
