package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryRequest {

    @NotNull(message = "dealerId không được để trống")
    private Long dealerId;

    @NotNull(message = "vehicleId không được để trống")
    private Long vehicleId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được nhỏ hơn 0")
    @Max(value = 1000, message = "Số lượng không được vượt quá 1000") // có thể điều chỉnh tùy thực tế
    private Integer quantity;
}
