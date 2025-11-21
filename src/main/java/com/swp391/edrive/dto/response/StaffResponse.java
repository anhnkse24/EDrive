package com.swp391.edrive.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Set<String> roles;
    private Long dealerId;
    private String dealerName;
    private boolean isActive;
}

