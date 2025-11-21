package com.swp391.edrive.dto.request;


import com.swp391.edrive.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuotationRequest {
    private Long vehicleId;            // ID của chiếc xe
    private Long customerId;           // ID của khách hàng
    private List<Long> selectedServiceIds; // Danh sách ID các dịch vụ được chọn

    private List<Long> selectedPromotionIds; // Danh sách ID các promotion được nhân viên chọn
    private String note;

}
