package com.swp391.edrive.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DealerInventoryResponse {
    private Long vehicleId;
    private String modelName;
    private String version;
    private String color;
    private Integer quantity;
    private LocalDateTime lastUpdated;
}