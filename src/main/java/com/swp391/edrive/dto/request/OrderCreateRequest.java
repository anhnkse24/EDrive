package com.swp391.edrive.dto.request;

import java.time.LocalDate;

public class OrderCreateRequest {
    public Long quotationId;        // có thể null (nếu lên từ báo giá)
    public Long vehicleId;          // có thể null (khi có quotationId)
    public Integer quantity;        // > 0
    public LocalDate desiredDeliveryDate;
    public String deliveryNote;
    public String deliveryAddress;
}
