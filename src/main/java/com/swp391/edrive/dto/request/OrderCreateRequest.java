package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderCreateRequest {
    public Long vehicleId;              // có thể null
    public Integer quantity;            // > 0
    public LocalDate desiredDeliveryDate;
    public String deliveryNote;
    public String deliveryAddress;
    public PaymentMethod paymentMethod; // CASH | VNPAY
}
