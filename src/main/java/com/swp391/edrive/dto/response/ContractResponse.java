package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContractResponse {
    public Long contractId;
    public Long dealerId;
    public Long quotationId;
    public Long orderId;         // có thể null ở giai đoạn này
    public LocalDateTime signedAt;
    public BigDecimal contractValue;
    public String terms;
    public ContractStatus status;

    // Thêm snapshot hữu ích
    public String note;          // lấy từ quotation (nếu muốn)
    public String quotationStatus;
    public String dealerName;
}
