package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.VehicleStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class VehicleUpsertRequest {

    @NotBlank(message = "Model không được để trống")
    @Size(max = 100, message = "Model tối đa 100 ký tự")
    private String modelName;

    @NotBlank(message = "Phiên bản không được để trống")
    @Size(max = 100, message = "Phiên bản tối đa 100 ký tự")
    private String version;

    @NotBlank(message = "Màu không được để trống")
    @Size(max = 50, message = "Màu tối đa 50 ký tự")
    @Pattern(regexp = "^[A-Za-zÀ-ỹ\\s]+$", message = "Màu chỉ được chứa chữ và khoảng trắng")
    private String color;

    @NotNull(message = "Dung lượng pin bắt buộc")
    @Min(value = 5, message = "Dung lượng pin tối thiểu 5 kWh")
    @Max(value = 300, message = "Dung lượng pin tối đa 300 kWh")
    private Integer batteryCapacityKwh;

    @NotNull(message = "Quãng đường bắt buộc")
    @Min(value = 10, message = "Quãng đường tối thiểu 10 km")
    @Max(value = 2000, message = "Quãng đường tối đa 2000 km")
    private Integer rangeKm;

    @NotNull(message = "Tốc độ tối đa bắt buộc")
    @Min(value = 10, message = "Tốc độ tối thiểu 10 km/h")
    @Max(value = 500, message = "Tốc độ tối đa 500 km/h")
    private Integer maxSpeedKmh;

    // Dùng BigDecimal để tránh sai số nhị phân
    @NotNull(message = "Thời gian sạc bắt buộc")
    @DecimalMin(value = "0.1", inclusive = true, message = "Thời gian sạc tối thiểu 0.1 giờ")
    @DecimalMax(value = "72.0", inclusive = true, message = "Thời gian sạc tối đa 72 giờ")
    private Float chargingTimeHours;

    @NotBlank(message = "Số chỗ ngồi bắt buộc")
    @Pattern(regexp = "4|7", message = "Chỉ chấp nhận xe 4 chỗ hoặc 7 chỗ")
    private String seatingCapacity;

    @NotNull(message = "Công suất mô tơ bắt buộc")
    @Min(value = 1, message = "Công suất mô tơ tối thiểu 1 kW")
    @Max(value = 1500, message = "Công suất mô tơ tối đa 1500 kW")
    private Integer motorPowerKw;

    @NotNull(message = "Khối lượng bắt buộc")
    @Min(value = 100, message = "Khối lượng tối thiểu 100 kg")
    @Max(value = 10000, message = "Khối lượng tối đa 10000 kg")
    private Integer weightKg;

    @NotNull(message = "Chiều dài bắt buộc")
    @Min(value = 500, message = "Chiều dài tối thiểu 500 mm")
    @Max(value = 10000, message = "Chiều dài tối đa 10000 mm")
    private Integer lengthMm;

    @NotNull(message = "Chiều rộng bắt buộc")
    @Min(value = 300, message = "Chiều rộng tối thiểu 300 mm")
    @Max(value = 5000, message = "Chiều rộng tối đa 5000 mm")
    private Integer widthMm;

    @NotNull(message = "Chiều cao bắt buộc")
    @Min(value = 300, message = "Chiều cao tối thiểu 300 mm")
    @Max(value = 5000, message = "Chiều cao tối đa 5000 mm")
    private Integer heightMm;

    @NotNull(message = "Giá bán lẻ bắt buộc")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải > 0")
    @Digits(integer = 12, fraction = 2, message = "Giá tối đa 12 số nguyên và 2 số thập phân")
    private BigDecimal priceRetail;

    @NotNull(message = "Trạng thái bắt buộc")
    private VehicleStatus status;

    private String imageUrl;


    @NotNull(message = "Năm sản xuất bắt buộc")
    @Min(value = 1950, message = "Năm sản xuất không trước 1950")
    @Max(value = 2025, message = "Năm sản xuất không vượt quá 2025")
    private Integer manufactureYear;
}
