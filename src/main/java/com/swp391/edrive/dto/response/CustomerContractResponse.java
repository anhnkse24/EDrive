package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response cho hợp đồng giữa Khách hàng và Đại lý
 */
@Data
@Builder
@Getter
@Setter
public class CustomerContractResponse {
    private Long id;
    private String contractCode;
    private String orderId;

    // Thông tin khách hàng
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;

    // Thông tin đại lý
    private Long dealerId;
    private String dealerName;
    private String dealerManagerName;  // Tên quản lý đại lý
    private String dealerPhone;         // Số điện thoại đại lý
    private String dealerEmail;         // Email đại lý

    // Thông tin xe
    private String vehicleModel;
    private String vehicleVersion;
    private String colorName;

    // Chi phí chi tiết
    private BigDecimal subtotal;          // Giá gốc
    private BigDecimal discountAmount;    // Số tiền giảm giá
    private BigDecimal vatAmount;         // VAT 10%
    private BigDecimal totalPrice;        // Tổng giá trị (100%)
    private BigDecimal depositAmount;     // Tiền cọc 7%
    private BigDecimal remainingAmount;   // Số tiền còn lại phải thanh toán (93%)

    // Thông tin hợp đồng
    private String terms;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String pdfUrl;
    private LocalDateTime pdfUploadedAt;
}

