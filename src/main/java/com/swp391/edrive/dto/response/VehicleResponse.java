package com.swp391.edrive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class VehicleResponse {
    private Long vehicleId;
    private String modelName;
    private String version;
    private String color;
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
    private Double priceRetail;
    private String status;
    private Integer manufactureYear;
}
