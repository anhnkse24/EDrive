package com.swp391.edrive.dto.request;


import java.time.LocalDate;

import jakarta.validation.constraints.*;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {
    @NotBlank(message = "Account cannot be blank")
    @Size(min = 6, max = 28, message = "Username must be between 6-28 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Confirm Password cannot be blank")
    private String confirmPassword;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone Number cannot be blank")
    @Pattern(regexp = "\\d{10}", message = "Invalid phone number")
    private String phone;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Full name must be between 2–50 characters")
    private String fullName;

    @NotBlank(message = "Dealer name is required")
    @Size(min = 4, max = 100, message = "Dealer name must be between 4–100 characters")
    private String dealerName;

    @NotBlank(message = "House number and street are required")
    @Size(min = 5, max = 150, message = "House number and street must be between 5–150 characters")
    private String houseNumberAndStreet;

    @NotBlank(message = "Ward or commune is required")
    @Size(max = 100, message = "Ward or commune must not exceed 100 characters")
    private String wardOrCommune;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @NotBlank(message = "Province or city is required")
    @Size(max = 100, message = "Province or city must not exceed 100 characters")
    private String provinceOrCity;
}
