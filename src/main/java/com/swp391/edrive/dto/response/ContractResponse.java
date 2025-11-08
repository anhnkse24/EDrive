package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class ContractResponse {
    private String orderId;

    private Long id;
    private String contractCode;

    private Long dealerId;
    private String dealerName;
    private String userFullName;

    private String manufacturerName;

    private String vehicleModel;
    private String vehicleVersion;
    private String colorName;
    private BigDecimal subtotal;
    private BigDecimal vatAmount;

    private BigDecimal totalPrice;
    private BigDecimal discountRate;
    private String terms;

    private String status;
    private String manufacturerNote;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String pdfUrl;
    private LocalDateTime pdfUploadedAt;
}
