package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String fullName;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dob;

    @NotBlank(message = "Giới tính không được để trống")
    @Pattern(regexp = "Nam|Nữ|Khác", message = "Giới tính chỉ được là 'Nam', 'Nữ' hoặc 'Khác'")
    private String gender;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10 chữ số")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @NotBlank(message = "Số CCCD/CMND không được để trống")
    @Pattern(regexp = "^[0-9]{9,12}$", message = "Số CCCD/CMND phải gồm 9–12 chữ số")
    private String idCardNo;
}
