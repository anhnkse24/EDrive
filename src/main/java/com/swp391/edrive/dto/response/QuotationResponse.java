package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.QuotationStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class QuotationResponse {
    public Long id;
    public Long dealerId;
    public Long customerId;
    public LocalDateTime createdAt;
    public LocalDate validUntil;
    public QuotationStatus status; // DRAFT, SENT, ...
    public BigDecimal grandTotal;
    public String note;
    public List<QuotationItemResponse> items;
}
