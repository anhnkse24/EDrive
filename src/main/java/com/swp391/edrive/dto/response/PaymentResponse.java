package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.PaymentMethod;
import com.swp391.edrive.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentResponse {
    public Long paymentId;
    public Long orderId;
    public BigDecimal amount;
    public LocalDate paymentDate;
    public PaymentType paymentType;
    public PaymentMethod method;
    public String note;
}
