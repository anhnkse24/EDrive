package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateContractFromQuotationRequest     {
    @NotNull
    public Long quotationId;
    // Optional nếu bạn muốn override, còn mặc định sẽ lấy theo quotation.getDealer()
    public Long dealerId;
    public String terms;  // điều khoản (text)
}
