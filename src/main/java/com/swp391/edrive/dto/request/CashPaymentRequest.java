package com.swp391.edrive.dto.request;

import java.math.BigDecimal;

public class CashPaymentRequest {
    public Long orderId;
    /** Nếu null sẽ tự thu phần còn lại */
    public BigDecimal amount;
    public String note; // optional, nếu Payment có field note thì dùng; nếu không thì bỏ qua
}
