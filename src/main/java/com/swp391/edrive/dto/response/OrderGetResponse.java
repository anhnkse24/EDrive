package com.swp391.edrive.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class OrderGetResponse {
    public Long orderId;
    public LocalDate orderDate;
    public Long dealerId;

    public String orderStatus;       // PENDING/PROCESSING/...
    public String paymentStatus;     // PENDING/PAID/...
    public String paymentMethod;     // FULL (Order dùng PaymentType)

    public BigDecimal grandTotal;    // tổng tiền (đã trừ CK + cộng VAT)
    public BigDecimal collected;     // tổng đã thu (CASH/VNPAY)
    public BigDecimal remaining;     // còn lại phải thu (>= 0)

    // nếu bạn có các field giao hàng trong Order thì thêm ở đây
    public String deliveryAddress;
    public String deliveryNote;
    public LocalDate desiredDeliveryDate;
}
