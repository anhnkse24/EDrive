package com.swp391.edrive.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractRequest {
    private Long dealerId;
    private String orderId;
    private String terms;
}
