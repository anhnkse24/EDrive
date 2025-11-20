package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiscountPolicyRequest {

    @NotNull(message = "Min quantity is required")
    @Min(value = 0, message = "Min quantity must be at least 0")
    private Integer minQuantity;

    @NotNull(message = "Max quantity is required")
    @Min(value = 1, message = "Max quantity must be at least 1")
    private Integer maxQuantity;

    @NotNull(message = "Discount rate is required")
    @DecimalMin(value = "0.00", message = "Discount rate must be at least 0")
    @DecimalMax(value = "1.00", message = "Discount rate must not exceed 1.00 (100%)")
    private BigDecimal discountRate; // Ví dụ: 0.05 = 5%, 0.15 = 15%

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}

