package com.swp391.edrive.dto.request;

import lombok.Data;

@Data
public class QuotationStatusUpdateRequest {
    private Long quotationId;
    private String status;  // ACCEPTED hoặc REJECTED
    private String rejectionReason;  // Lý do từ chối (nếu REJECTED)
}

