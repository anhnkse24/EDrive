package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.OrderStatus;
import com.swp391.edrive.enums.PaymentStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
public class OrderResponse {
    private String orderId;
    private Long dealerId;
    private String dealerName;

    private LocalDate orderDate;
    private LocalDate desiredDeliveryDate;
    private LocalDate actualDeliveryDate;

    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal vatAmount;
    private BigDecimal totalPrice;

    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;

    private String deliveryAddress;
    private String deliveryNote;

    private List<OrderItemResponse> orderItems;

}
