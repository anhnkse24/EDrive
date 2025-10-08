package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.VehicleStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VehicleUpsertRequest {
    @NotBlank
    @Size(max = 100)
    private String modelName;

    @NotBlank
    @Size(max = 100)
    private String version;

    @NotBlank
    @Size(max = 50)
    private String color;

    @Positive
    @NotNull
    private Integer batteryCapacityKwh;

    @Positive
    @NotNull
    private Integer rangeKm;

    @Positive
    @NotNull
    private Integer maxSpeedKmh;

    @Positive
    @NotNull
    private Float chargingTimeHours;

    @Positive
    @NotNull
    private Integer seatingCapacity;

    @Positive
    @NotNull
    private Integer motorPowerKw;

    @Positive
    @NotNull
    private Integer weightKg;

    @Positive
    @NotNull
    private Integer lengthMm;

    @Positive
    @NotNull
    private Integer widthMm;

    @Positive
    @NotNull
    private Integer heightMm;

    @Positive
    @NotNull
    private BigDecimal priceRetail;

    @NotNull
    private VehicleStatus status;

    @Min(1950)
    @Max(2100)
    @NotNull
    private Integer manufactureYear;
}
