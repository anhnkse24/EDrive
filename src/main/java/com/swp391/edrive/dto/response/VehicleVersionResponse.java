package com.swp391.edrive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Getter
public class VehicleVersionResponse {
    private Long versionId;
    private Long modelId;
    private String modelName;
    private String versionName;

    private Integer batteryCapacityKwh;
    private Integer rangeKm;
    private Integer maxSpeedKmh;
    private Float chargingTimeHours;
    private Integer seatingCapacity;
    private Integer motorPowerKw;
    private Integer weightKg;
    private Integer lengthMm;
    private Integer widthMm;
    private Integer heightMm;
    private BigDecimal basePrice;
    private String status;
    private Integer manufactureYear;

    // thêm màu
    private Long colorId;
    private String colorName;
    private String colorHex;   // nếu có
    private String colorCode;
}
