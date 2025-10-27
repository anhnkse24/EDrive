package com.swp391.edrive.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrderCreateRequest {
    public List<OrderItemRequest> orderItems;    // Danh sách các loại xe
    public LocalDate desiredDeliveryDate;
    public String deliveryNote;
    public String deliveryAddress;
}

