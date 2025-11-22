package com.swp391.edrive.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
    public String orderId;

    // ==== GIÁ TIỀN ====
    public BigDecimal subtotal;        // Tạm tính (chưa VAT, chưa chiết khấu)
    public BigDecimal dealerDiscount;  // Tổng chiết khấu
    public BigDecimal vatAmount;       // VAT
    public BigDecimal grandTotal;      // Tổng thanh toán cuối cùng (100%)
    public BigDecimal depositAmount;   // Tiền cọc 7%
    public BigDecimal remainingAmount; // Số tiền còn lại phải thanh toán (93%)

    // ==== THÔNG TIN ĐƠN HÀNG ====
    public LocalDate desiredDeliveryDate;
    public String deliveryAddress;
    public String deliveryNote;

    public String orderStatus;
    public String paymentStatus;

    // ==== CHI TIẾT TỪNG MẶT HÀNG ====
    public List<OrderItemResponse> orderItems;
}

