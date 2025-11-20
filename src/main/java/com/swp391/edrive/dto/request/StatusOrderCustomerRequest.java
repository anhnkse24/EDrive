package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusOrderCustomerRequest {

    @NotBlank(message = "Trạng thái không được để trống")
    @Size(max = 100, message = "Trạng thái tối đa 100 ký tự")
    private String status;

    @Pattern(
            regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$",
            message = "Ngày giao xe phải có định dạng yyyy-MM-dd hoặc để trống"
    )
    private String deliveryDate;

    @NotBlank(message = "Địa điểm giao xe không được để trống")
    @Size(max = 200, message = "Địa điểm giao xe tối đa 200 ký tự")
    private String deliveryLocation;

}
