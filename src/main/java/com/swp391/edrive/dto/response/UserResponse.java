package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private String dealerName;
}