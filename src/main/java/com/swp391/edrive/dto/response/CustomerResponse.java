package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.Gender;
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
    private Gender gender;
    private String email;
    private String phone;
    private String address;
    private String idCardNo;

    private Long dealerId;
    private String dealerName;
}
