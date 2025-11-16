package com.swp391.edrive.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealerInventoryDTO {
    private String modelName;
    private String version;
    private String colorName;
    private Integer quantity;

    public DealerInventoryDTO(String modelName, String version, String colorName, Integer quantity) {
        this.modelName = modelName;
        this.version = version;
        this.colorName = colorName;
        this.quantity = quantity;
    }
}

