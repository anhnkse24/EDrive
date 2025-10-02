package com.swp391.edrive.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRequest {
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String idCardNo;
}
