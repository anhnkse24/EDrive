package com.swp391.edrive.dto.request;


import com.swp391.edrive.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotationRequest {
    private Long vehicleId;            // ID của chiếc xe
    private Long customerId;           // ID của khách hàng (không cần truyền đối tượng Customer)
    private PaymentMethod paymentMethod; // Phương thức thanh toán (Trả thẳng, trả góp)
    private AdditionalServicesRequest additionalServices; // Dịch vụ bổ sung
}
