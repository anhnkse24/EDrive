package com.swp391.edrive.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private Long customerId;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String idCardNo;
}
