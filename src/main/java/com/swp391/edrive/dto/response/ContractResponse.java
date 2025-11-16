package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response chung cho các API internal contract
 * @deprecated Nên dùng ManufacturerContractResponse hoặc CustomerContractResponse thay thế
 */
@Data
@Builder
@Getter
@Setter
@Deprecated
public class ContractResponse {
    private String orderId;

    private Long id;
    private String contractCode;

    // Thông tin đại lý
    private Long dealerId;
    private String dealerName;
    private String dealerManagerName;  // Tên quản lý đại lý

    private String manufacturerName;

    // Thông tin xe
    private String vehicleModel;
    private String vehicleVersion;
    private String colorName;

    // Chi phí
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

    // Signature fields
    private String manufacturerSignatureData;
    private LocalDateTime manufacturerSignedAt;
    private String dealerSignatureData;
    private LocalDateTime dealerSignedAt;
}
