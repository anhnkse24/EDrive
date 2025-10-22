package com.swp391.edrive.dto.response;

import java.math.BigDecimal;

public class QuotationItemResponse {
    public Long id;
    public Long versionColorId;  // có thể null nếu bạn không link
    public String modelName;
    public String versionName;
    public String colorName;
    public Integer quantity;
    public BigDecimal unitPrice;
    public BigDecimal lineTotal;
}
