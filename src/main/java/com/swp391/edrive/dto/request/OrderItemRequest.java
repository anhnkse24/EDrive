package com.swp391.edrive.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    public Long vehicleId;
    public Integer quantity;
}