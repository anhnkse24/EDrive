package com.swp391.edrive.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuotationToOrderRequest {
    private Long quotationId;  // ID của báo giá cần chuyển thành order
    private LocalDate desiredDeliveryDate;  // Ngày giao hàng mong muốn
    private String deliveryAddress;  // Địa chỉ giao hàng
    private String deliveryNote;  // Ghi chú giao hàng
}

