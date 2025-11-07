package com.swp391.edrive.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CashPaymentResponse {
    public Long orderId;
    public BigDecimal paidNow;
    public BigDecimal totalCollected;
    public BigDecimal grandTotal;
    public BigDecimal remaining;
    public BigDecimal changeAmount;

    public String orderStatus;
    public String paymentStatus;
}
