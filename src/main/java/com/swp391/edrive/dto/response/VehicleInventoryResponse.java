package com.swp391.edrive.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleInventoryResponse {
    private Long vehicleId;
    private String vehicleName;
    private Integer quantity;
}
