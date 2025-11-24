package com.swp391.edrive.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResponse {
    private Long quotationId;

    // Thông tin đại lý và người tạo
    private Long dealerId;
    private String dealerName;
    private String createdByUserName;  // Tên người tạo báo giá

    // Thông tin xe đầy đủ
    private Long vehicleId;
    private String modelName;
    private String version;
    private Integer batteryCapacityKwh;
    private Integer rangeKm;
    private Integer maxSpeedKmh;
    private Float chargingTimeHours;
    private Integer seatingCapacity;
    private Integer motorPowerKw;
    private Integer weightKg;
    private Integer lengthMm;
    private Integer widthMm;
    private Integer heightMm;
    private String imageUrl;
    private Integer manufactureYear;

    // Thông tin khách hàng đầy đủ
    private Long customerId;
    private String customerFullName;
    private LocalDate customerDob;
    private String customerGender;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private String customerIdCardNo;

    // Thông tin thanh toán
    private String paymentMethod;
    private String quotationStatus;
    private String quotationStatusCustomer;
    private String deliveryStatus;

    // Dịch vụ bổ sung (MỚI - danh sách dịch vụ động)
    private List<SelectedServiceResponse> selectedServices;

    // Khuyến mãi đã áp dụng
    private List<AppliedPromotionResponse> appliedPromotions;

    // Chi tiết giá
    private BigDecimal unitPrice;
    private BigDecimal promotionDiscountAmount; // Giá giảm từ khuyến mãi
    private BigDecimal additionalServicesTotal; // Tổng giá dịch vụ bổ sung
    private BigDecimal vatAmount;               // Phí VAT (10%)
    private BigDecimal grandTotal;              // Tổng giá cuối cùng

    private String note;

}
