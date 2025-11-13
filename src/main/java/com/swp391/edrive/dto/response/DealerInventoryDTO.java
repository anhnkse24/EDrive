package com.swp391.edrive.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealerInventoryDTO {
    private Long vehicleId;
    private String modelName;
    private String version;
    private String colorName;
    private Integer quantity;

    public DealerInventoryDTO(Long vehicleId, String modelName, String version, String colorName, Integer quantity) {
        this.vehicleId = vehicleId;
        this.modelName = modelName;
        this.version = version;
        this.colorName = colorName;
        this.quantity = quantity;
    }
}

