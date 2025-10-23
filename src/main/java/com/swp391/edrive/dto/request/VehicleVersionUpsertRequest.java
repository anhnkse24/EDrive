package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.VehicleStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class VehicleVersionUpsertRequest {

    @NotNull(message = "modelId bắt buộc")
    private Long modelId;

    @NotBlank(message = "Phiên bản không được để trống")
    @Size(max = 100, message = "Phiên bản tối đa 100 ký tự")
    private String versionName;

    @NotNull @Min(5) @Max(300)
    private Integer batteryCapacityKwh;

    @NotNull @Min(10) @Max(2000)
    private Integer rangeKm;

    @NotNull @Min(10) @Max(500)
    private Integer maxSpeedKmh;

    @NotNull
    @DecimalMin("0.1") @DecimalMax("72.0") @Digits(integer = 3, fraction = 2)
    private BigDecimal chargingTimeHours;

    @NotNull @Min(1) @Max(20)
    private Integer seatingCapacity;

    @NotNull @Min(1) @Max(1500)
    private Integer motorPowerKw;

    @NotNull @Min(100) @Max(10000)
    private Integer weightKg;

    @NotNull @Min(500) @Max(10000)
    private Integer lengthMm;

    @NotNull @Min(300) @Max(5000)
    private Integer widthMm;

    @NotNull @Min(300) @Max(5000)
    private Integer heightMm;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 14, fraction = 2)
    private BigDecimal basePrice;

    @NotNull
    private VehicleStatus status;

    @NotNull @Min(1950) @Max(2050)
    private Integer manufactureYear;
}
