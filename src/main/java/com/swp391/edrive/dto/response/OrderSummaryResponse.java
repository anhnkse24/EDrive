package com.swp391.edrive.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderSummaryResponse {
    public Long orderId;

    // ==== GIÁ TIỀN ====
    public BigDecimal subtotal;       // Tạm tính (chưa VAT, chưa chiết khấu)
    public BigDecimal dealerDiscount; // Chiết khấu
    public BigDecimal vatAmount;      // VAT
    public BigDecimal grandTotal;     // Tổng thanh toán cuối cùng

    // ==== THÔNG TIN KHÁC ====
    public LocalDate desiredDeliveryDate;
    public String deliveryAddress;
    public String deliveryNote;

    public String orderStatus;
    public String paymentStatus;
    public String paymentMethod;

}
