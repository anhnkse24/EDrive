package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotationCreateRequest {

    @NotNull(message = "Vui lòng chọn xe cần báo giá.")
    private Long vehicleId;

    private boolean includeInsurancePercent;
    private boolean includeWarrantyExtension;
    private boolean includeAccessories;

    @NotBlank(message = "Họ tên khách hàng không được để trống.")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự.")
    private String customerFullName;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Pattern(
            regexp = "^(0[3|5|7|8|9])[0-9]{8}$",
            message = "Số điện thoại không hợp lệ (phải là số di động Việt Nam, gồm 10 chữ số)."
    )
    private String phone;

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Định dạng email không hợp lệ.")
    @Size(max = 150, message = "Email tối đa 150 ký tự.")
    private String email;

    @NotBlank(message = "Số nhà và tên đường không được để trống.")
    @Size(max = 150, message = "Số nhà và tên đường tối đa 150 ký tự.")
    private String street;

    @NotBlank(message = "Phường/Xã không được để trống.")
    @Size(max = 100, message = "Phường/Xã tối đa 100 ký tự.")
    private String ward;

    @NotBlank(message = "Quận/Huyện không được để trống.")
    @Size(max = 100, message = "Quận/Huyện tối đa 100 ký tự.")
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống.")
    @Size(max = 100, message = "Tỉnh/Thành phố tối đa 100 ký tự.")
    private String city;

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự.")
    private String notes;
}
