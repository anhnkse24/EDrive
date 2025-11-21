package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffUpdateRequest {
    @Email(message = "Email không hợp lệ")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Size(min = 2, max = 50, message = "Họ tên phải từ 2–50 ký tự")
    private String fullName;
}

