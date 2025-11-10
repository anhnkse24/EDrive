package com.swp391.edrive.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnverifiedUserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String dealerName;
    private String dealerAddress;
    private String businessLicenseUrl;
    private LocalDateTime registrationDate;
    private boolean isVerified;
}
