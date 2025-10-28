package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class QuotationResponse {

    // ====== Thông tin chung ======
    private Long quotationId;

    // ====== Thông tin xe ======
    private Long vehicleId;
    private String vehicleModel;     // Ví dụ: "VF 8 - Plus"
    private String vehicleImageUrl;  // Nếu Vehicle có trường imageUrl
    private BigDecimal unitPrice;    // Giá xe tại thời điểm báo giá

    // ====== Dịch vụ chính hãng ======
    private boolean includeInsurancePercent;   // +3% giá xe
    private boolean includeWarrantyExtension;  // +50,000,000
    private boolean includeAccessories;        // +30,000,000

    // ====== Thông tin tính giá ======
    private BigDecimal discountRate;           // Ví dụ 0.05 (5%)
    private BigDecimal discountAmount;         // unitPrice * discountRate
    private BigDecimal vehicleSubtotal;        // Giá xe trước chiết khấu
    private BigDecimal serviceTotal;           // Tổng dịch vụ cộng thêm
    private BigDecimal subtotalAfterDiscount;  // Sau khi trừ chiết khấu
    private BigDecimal taxableBase;            // Cộng thêm dịch vụ để tính VAT
    private BigDecimal vatRate;                // Ví dụ 0.10
    private BigDecimal vatAmount;              // Làm tròn 0 số
    private BigDecimal grandTotal;             // Tổng cộng cuối cùng (sau VAT)

    // ====== Thông tin khách hàng ======
    private String customerFullName;
    private String phone;
    private String email;
    private String fullAddress; // Ghép: "số nhà, phường/xã, quận/huyện, tỉnh/thành phố"
    private String notes;
}
