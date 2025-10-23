package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {
    @NotNull
    public String status; // PENDING/PROCESSING/DELIVERED/CANCELLED
}
