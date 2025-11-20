package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @Size(max = 120)
    private String email;

    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại không hợp lệ (phải có 10 chữ số và bắt đầu bằng 0)")
    private String phone;

    @Size(max = 100, message = "Tên đại lý không được vượt quá 100 ký tự")
    private String agencyName;

    @Size(max = 100, message = "Tên người liên hệ không được vượt quá 100 ký tự")
    private String contactPerson;

    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại đại lý không hợp lệ (phải có 10 chữ số và bắt đầu bằng 0)")
    private String agencyPhone;

    @Size(max = 150)
    private String streetAddress;

    @Size(max = 100)
    private String ward;

    @Size(max = 100)
    private String district;

    @Size(max = 100)
    private String city;

    @Size(max = 255)
    private String fullAddress;
}
