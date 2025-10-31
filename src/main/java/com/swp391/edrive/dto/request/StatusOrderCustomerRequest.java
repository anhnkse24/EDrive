package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusOrderCustomerRequest {

    // 🟢 Trạng thái đơn hàng — bắt buộc
    @NotBlank(message = "Trạng thái không được để trống")
    @Size(max = 100, message = "Trạng thái tối đa 100 ký tự")
    private String status; // Ví dụ: "Chờ xử lý", "Đang giao", "Đã giao", "Hủy đơn"

    // 🟡 Ngày giao xe — có thể để trống, nhưng nếu nhập phải đúng định dạng yyyy-MM-dd
    @Pattern(
            regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$",
            message = "Ngày giao xe phải có định dạng yyyy-MM-dd hoặc để trống"
    )
    private String deliveryDate;

    // 🟣 Địa điểm giao xe — bắt buộc, giới hạn độ dài
    @NotBlank(message = "Địa điểm giao xe không được để trống")
    @Size(max = 200, message = "Địa điểm giao xe tối đa 200 ký tự")
    private String deliveryLocation;

}
