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
    private PaymentMethod paymentMethod; // Phương thức thanh toán (Trả thẳng, trả góp)

    // Danh sách ID dịch vụ được chọn từ catalog
    private List<Long> selectedServiceIds; // Danh sách ID các dịch vụ được chọn
}
