package com.swp391.edrive.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    public String token;
    public String refreshToken;
}