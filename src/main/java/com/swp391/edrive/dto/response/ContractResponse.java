package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class ContractResponse {
    private Long id;
    private String contractCode;

    private Long dealerId;
    private String dealerName;

    private String manufacturerName;

    private String vehicleModel;
    private String vehicleVersion;
    private BigDecimal subtotal;
    private BigDecimal vatAmount;

    private BigDecimal totalPrice;
    private BigDecimal discountRate;
    private String terms;

    private String status;            // DRAFT, PENDING_MANUFACTURER, APPROVED, REJECTED
    private String manufacturerNote;  // ghi chú từ hãng

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
