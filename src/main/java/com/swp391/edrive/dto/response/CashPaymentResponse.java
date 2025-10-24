package com.swp391.edrive.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CashPaymentResponse {
    public Long orderId;
    public BigDecimal paidNow;         // số tiền khách đưa
    public BigDecimal totalCollected;  // tổng tiền đã thu (tích lũy)
    public BigDecimal grandTotal;      // tổng phải trả
    public BigDecimal remaining;       // còn thiếu (0 nếu đủ hoặc dư)
    public BigDecimal changeAmount;    // 💰 TIỀN THỐI LẠI (0 nếu không dư)

    public String orderStatus;
    public String paymentStatus;
}
