package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleColorImageRequest {

    @NotNull(message = "Color ID không được để trống")
    private Long colorId;

    @NotNull(message = "Image URL không được để trống")
    private String imageUrl;
}

