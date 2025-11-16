package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request dùng cho việc tạo mới và cập nhật kho xe của nhà sản xuất.
 */
@Getter
@Setter
public class ManufacturerInventoryRequest {

    @NotNull(message = "vehicleId không được để trống")
    private Long vehicleId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải >= 0")
    private Integer quantity;
}
