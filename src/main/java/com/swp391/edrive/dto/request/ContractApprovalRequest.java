package com.swp391.edrive.dto.request;

import lombok.Data;

@Data
public class ContractApprovalRequest {
    private Long contractId;
    private Boolean approved;
    private String rejectionReason;
}

