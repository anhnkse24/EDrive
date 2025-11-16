package com.swp391.edrive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDetailResponse {
    private Long id;
    private String orderId;
    private String status;
    
    private BuyerInfo buyer;
    private DealerInfo dealer;
    private ManufacturerInfo manufacturer;
    private PricingInfo pricing;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyerInfo {
        private String name;
        private String phone;
        private String address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DealerInfo {
        private Long id;
        private String name;
        private String phone;
        private String address;
        private String representative;
        private String signatureData;
        private LocalDateTime signedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManufacturerInfo {
        private String name;
        private String address;
        private String phone;
        private String taxCode;
        private String signatureData;
        private LocalDateTime signedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingInfo {
        private BigDecimal subtotal;
        private BigDecimal discount;
        private Integer taxPercent;
        private BigDecimal total;
    }
}
