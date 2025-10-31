package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCustomerRequest {

    // ==========================
    // 🔹 Thông tin khách hàng
    // ==========================
    @NotBlank(message = "Họ tên khách hàng không được để trống")
    @Size(max = 100, message = "Họ tên khách hàng tối đa 100 ký tự")
    private String customerName;

    @NotBlank(message = "Số điện thoại khách hàng không được để trống")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
    private String customerPhone;

    // ==========================
    // 🔹 Thông tin xe
    // ==========================
    @NotNull(message = "Xe không được để trống")
    private Long vehicleId; // ID xe mới nếu đổi

    @NotBlank(message = "Màu xe không được để trống")
    @Size(max = 50, message = "Tên màu xe tối đa 50 ký tự")
    private String color;

    // ==========================
    // 🔹 Trạng thái và giao xe
    // ==========================
    @NotBlank(message = "Trạng thái đơn hàng không được để trống")
    @Size(max = 50, message = "Trạng thái tối đa 50 ký tự")
    private String status; // VD: "Đã giao", "Chờ xử lý"

    @NotBlank(message = "Ngày giao xe không được để trống")
    private String deliveryDate; // ISO format: "2025-11-10" chẳng hạn

    @NotBlank(message = "Địa điểm giao xe không được để trống")
    @Size(max = 200, message = "Địa điểm giao xe tối đa 200 ký tự")
    private String deliveryLocation;

    // ==========================
    // 🔹 Đại lý
    // ==========================
    @NotNull(message = "Đại lý không được để trống")
    private Long dealerId; // Có thể đổi đại lý nếu cần
}
