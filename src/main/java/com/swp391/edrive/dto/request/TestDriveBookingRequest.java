package com.swp391.edrive.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TestDriveBookingRequest {
    @NotBlank
    @Size(max = 100) private String fullName;

    @NotBlank
    @Pattern(regexp = "^(0[0-9]{9})$") private String phone;

    @NotBlank
    @Email
    @Size(max = 100) private String email;

    @NotBlank
    @Pattern(regexp = "^[0-9]{9,12}$")
    private String idCardNo;

    @NotNull private Long dealerId;
    @NotNull private Long vehicleId;

    // ÉP ĐỊNH DẠNG yyyy-MM-dd
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull
    @Min(0)
    @Max(23)
    private Integer hour;

    // CHỈ CHO PHÉP 0 hoặc 30 cho slot 30'
    @NotNull
    @Min(0)
    @Max(59)
    private Integer minute;

    @Size(max = 500)
    private String note;

    @AssertTrue private Boolean agreePolicy;
}
