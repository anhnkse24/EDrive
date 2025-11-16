package com.swp391.edrive.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManufacturerInventoryResponse {
    private Long manufacturerInventoryId;
    private String manufacturerName;
    private Long vehicleId;
    private String vehicleName;
    private Integer quantity;
    private LocalDateTime lastUpdated;
}
