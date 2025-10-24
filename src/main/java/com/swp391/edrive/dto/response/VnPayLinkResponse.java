package com.swp391.edrive.dto.response;

import java.math.BigDecimal;

public class VnPayLinkResponse {
    public Long orderId;
    public BigDecimal amountToPay;
    public String paymentStatus;   // PROCESSING | PAID
    public String vnpPaymentUrl;   // link chuyển tới sandbox
    public String note;
}
