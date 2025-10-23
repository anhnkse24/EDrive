package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealerRequest {

    @NotBlank(message = "Tên đại lý không được để trống")
    @Size(max = 100, message = "Tên đại lý không được vượt quá 100 ký tự")
    private String dealerName;

    // --- Địa chỉ chi tiết ---
    @NotBlank(message = "Số nhà và tên đường không được để trống")
    @Size(max = 100, message = "Số nhà và tên đường không được vượt quá 100 ký tự")
    private String houseNumberAndStreet;

    @Size(max = 100, message = "Phường/Xã không được vượt quá 100 ký tự")
    private String wardOrCommune;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Size(max = 100, message = "Quận/Huyện không được vượt quá 100 ký tự")
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Size(max = 100, message = "Tỉnh/Thành phố không được vượt quá 100 ký tự")
    private String provinceOrCity;

    // --- Thông tin liên hệ ---
    @NotBlank(message = "Người liên hệ không được để trống")
    @Size(max = 100, message = "Tên người liên hệ không được vượt quá 100 ký tự")
    private String contactPerson;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^(\\+?\\d{1,3})?\\s?\\d{8,15}$",
            message = "Số điện thoại không hợp lệ (ví dụ: 090xxxxxxx hoặc +8490xxxxxxx)"
    )
    private String phone;

    // --- Hàm tiện ích: tạo địa chỉ đầy đủ ---
    public String getFullAddress() {
        return String.format("%s, %s, %s, %s",
                houseNumberAndStreet, wardOrCommune, district, provinceOrCity);
    }
}
