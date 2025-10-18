package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleModelUpsertRequest {
    @NotBlank
    @Size(max = 100)
    private String modelName;

    @Size(max = 255)
    private String description;

    @Size(max = 255)
    private String imageUrl;
}
