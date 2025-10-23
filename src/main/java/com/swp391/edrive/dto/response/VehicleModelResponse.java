package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VehicleModelResponse {
    Long id;
    String modelName;
    String description;
    String imageUrl;
    Integer versionCount; // Số lượng phiên bản thuộc về mẫu xe này
}
