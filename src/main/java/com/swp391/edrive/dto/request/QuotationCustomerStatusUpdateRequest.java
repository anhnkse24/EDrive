package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.CustomerQuotationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotationCustomerStatusUpdateRequest {
    private Long quotationId;
    private CustomerQuotationStatus customerStatus; // APPROVED hoặc REJECTED
}