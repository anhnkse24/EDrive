package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.VehicleStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {
    private Long versionId;
    private Long modelId;
    private String modelName;
    private String versionName;

    private Integer batteryCapacityKwh;
    private Integer rangeKm;
    private Integer maxSpeedKmh;
    private Float   chargingTimeHours;
    private Integer seatingCapacity;
    private Integer motorPowerKw;
    private Integer weightKg;
    private Integer lengthMm;
    private Integer widthMm;
    private Integer heightMm;

    private BigDecimal basePrice;
    private Integer manufactureYear;
    private VehicleStatus status;

    private List<ColorBriefResponse> colors;
}
