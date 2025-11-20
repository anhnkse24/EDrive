package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.QuotationKind;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CreateQuotationRequest {
    public Long dealerId;
    public Long customerId;
    public QuotationKind kind;       // RETAIL hoặc PURCHASE (mặc định PURCHASE nếu null)
    public LocalDate validUntil;
    public String note;
    public List<Item> items;
    public static class Item {
        public Long versionColorId;
        public Integer quantity;
        public BigDecimal unitPrice;
    }
}
