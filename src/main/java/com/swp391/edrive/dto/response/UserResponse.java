package com.swp391.edrive.dto.response;

import com.swp391.edrive.enums.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private DealerResponse dealer;
}