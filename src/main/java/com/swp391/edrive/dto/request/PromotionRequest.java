package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.DiscountType;
import com.swp391.edrive.enums.PromoTarget;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class PromotionRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be greater than 0")
    private Double discountValue;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Applicable target is required")
    private PromoTarget applicableTo;

    private Set<Long> vehicleIds;

    private Set<Long> customerIds;
}