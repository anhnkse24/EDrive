package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InventoryResponse {
    private Long inventoryId;
    private Long dealerId;
    private String dealerName;
    private Long vehicleId;
    private String vehicleModel;
    private Integer quantity;
    private LocalDateTime lastUpdated;
}
