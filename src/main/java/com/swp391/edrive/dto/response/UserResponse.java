package com.swp391.edrive.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
    private Long dealerId;
    private String token;
    private String refreshToken;
}
