package com.swp391.edrive.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderSummaryResponse {
    public Long orderId;
    public BigDecimal unitPrice;
    public Integer quantity;
    public BigDecimal subtotal;
    public BigDecimal dealerDiscount;
    public BigDecimal vatAmount;
    public BigDecimal grandTotal;

    public LocalDate desiredDeliveryDate;
    public String deliveryAddress;
    public String deliveryNote;

    public String orderStatus;      // PENDING/PROCESSING/...
    public String paymentMethod;    // CASH
    public String paymentStatus;    // PAID
}
