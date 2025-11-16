package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractRequest {
    @NotNull(message = "dealerId không được để trống")
    @Positive(message = "dealerId phải là số dương")
    private Long dealerId;

    @NotBlank(message = "orderId không được để trống")
    @Size(max = 50, message = "orderId không được vượt quá 50 ký tự")
    private String orderId;

    @NotBlank(message = "terms không được để trống")
    @Size(max = 1000, message = "terms không được vượt quá 1000 ký tự")
    private String terms;

}
