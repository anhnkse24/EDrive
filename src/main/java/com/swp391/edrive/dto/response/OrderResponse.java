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
    private BigDecimal totalPrice;       // Tổng giá trị order (100%)
    private BigDecimal depositAmount;    // Tiền cọc 7%
    private BigDecimal remainingAmount;  // Số tiền còn lại phải thanh toán (93%)

    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;

    private String deliveryAddress;
    private String deliveryNote;

    private AdditionalServicesResponse additionalServices;

    private List<OrderItemResponse> orderItems;

}
