package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response cho hợp đồng giữa Hãng và Đại lý
 * Sử dụng khi: Đại lý mua xe từ hãng
 */
@Data
@Builder
@Getter
@Setter
public class ManufacturerContractResponse {
    private String orderId;

    private Long id;
    private String contractCode;

    // Thông tin đại lý
    private Long dealerId;
    private String dealerName;
    private String dealerManagerName;  // Tên quản lý đại lý (contactPerson)
    private String dealerPhone;         // Số điện thoại đại lý
    private String dealerEmail;         // Email đại lý

    // Thông tin hãng
    private String manufacturerName;
    private String manufacturerAdminName;   // Tên admin hãng
    private String manufacturerAdminPhone;  // Số điện thoại admin hãng
    private String manufacturerAdminEmail;  // Email admin hãng

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
}

