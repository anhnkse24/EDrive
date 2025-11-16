package com.swp391.edrive.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManufacturerInventorySummaryResponse {
    private String manufacturerName;
    private Integer totalQuantity;
    private List<VehicleInventoryResponse> vehicles;
}
