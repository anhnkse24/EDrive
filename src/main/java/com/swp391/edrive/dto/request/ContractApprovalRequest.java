package com.swp391.edrive.dto.request;

import lombok.Data;

@Data
public class ContractApprovalRequest {
    private Long contractId;
    private Boolean approved;  // true = approve, false = reject
    private String rejectionReason;  // Lý do từ chối (nếu reject)
}

