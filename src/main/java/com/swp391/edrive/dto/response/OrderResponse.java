package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentMethod;
import com.swp391.edrive.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    public Long orderId;
    public Long contractId;
    public Long quotationId;
    public Long dealerId;
    public Long customerId;
    public LocalDate orderDate;
    public BigDecimal totalPrice;
    public PaymentType paymentType;
    public PaymentMethod paymentMethod;
    public OrderStatus status;

    // tiện ích
    public BigDecimal totalPaid;
    public BigDecimal remaining;
}
