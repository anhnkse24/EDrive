package com.swp391.edrive.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryRequest {
    private Long dealerId;
    private Long vehicleId;
    private Integer quantity;
}
