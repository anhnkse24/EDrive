package com.swp391.edrive.dto.request;

import lombok.Data;

@Data
public class ContractFromOrderRequest {
    private String orderId;  // ID của order cần tạo hợp đồng
}

