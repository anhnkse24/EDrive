package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
@SuppressWarnings("unused")
public class CreateOrderFromContractRequest {
    @NotNull public Long contractId;
    public PaymentType paymentType; // FULL | DEPOSIT | INSTALLMENT (enum)
    public String note;
}
