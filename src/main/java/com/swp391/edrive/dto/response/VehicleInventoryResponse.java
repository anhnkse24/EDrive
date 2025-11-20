package com.swp391.edrive.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleInventoryResponse {
    private Long manufacturerInventoryId;
    private Long vehicleId;
    private String vehicleName;
    private String version;
    private String color;
    private Integer quantity;
    private Integer exportedQuantity;
    private Integer inDeliveryQuantity;
    private List<DealerQuantityResponse> dealers;
}
