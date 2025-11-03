package com.swp391.edrive.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractRequest {
    private Long dealerId;        // Đại lý ký hợp đồng
    private Long vehicleId;       // Chọn xe theo danh sách Vehicle (dropdown)
    private BigDecimal totalPrice;   // Tổng giá trị hợp đồng (Dealer đề xuất)
    private BigDecimal discountRate; // Chiết khấu (Dealer đề xuất)
    private String terms;            // Điều khoản hợp đồng
}
