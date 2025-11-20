package com.swp391.edrive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private String id;
    private String code;
    private LocalDate orderDate;
    private LocalDate desiredDeliveryDate;
    
    private DealerInfo dealer;
    private CustomerInfo customer;
    private List<OrderItemInfo> orderItems;
    private MoneyInfo money;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DealerInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String name;
        private String phone;
        private String address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemInfo {
        private String vehicleName;
        private String color;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal itemSubtotal;
        private BigDecimal itemDiscount;
        private BigDecimal itemTotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoneyInfo {
        private BigDecimal subtotal;
        private BigDecimal discount;
        private Integer taxPercent;
        private BigDecimal fees;
        private BigDecimal total;
        private BigDecimal paidTotal;
        private BigDecimal remaining;
    }
}
