package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Full name must be between 2–50 characters")
    @Pattern(regexp = "^[a-zA-ZÀ-Ỹà-ỹ\\s]+$", message = "Full name can only contain letters and spaces")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Email must be a valid Gmail address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Phone must be 10 digits and start with 0")
    private String phone;

    @NotBlank(message = "Dealer name is required")
    @Size(min = 4, max = 40, message = "Dealer name must be between 4–40 characters")
    private String dealerName;

    @NotBlank(message = "House number and street are required")
    @Size(min = 5, max = 100, message = "House number and street must be between 5–100 characters")
    private String houseNumberAndStreet;

    @NotBlank(message = "Ward or commune is required")
    private String wardOrCommune;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Province or city is required")
    private String provinceOrCity;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4–20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String confirmPassword;
}
