package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateCashPaymentRequest {
    @NotNull public Long orderId;
    @NotNull public BigDecimal amount; // >0
    @NotNull
    public PaymentType paymentType; // FULL/DEPOSIT/INSTALLMENT
    public String note;
}
