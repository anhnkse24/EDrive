package com.swp391.edrive.dto.request;

import com.swp391.edrive.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRequest {
    @NotBlank
    @Size(max = 100)
    private String fullName;

    @Past
    private LocalDate dob;

    @NotNull
    private Gender gender;

    @NotBlank @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    private String phone;

    @NotBlank @Size(max = 200)
    private String address;

    @NotBlank
    private String idCardNo;
}
