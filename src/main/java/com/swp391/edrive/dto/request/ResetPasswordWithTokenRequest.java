package com.swp391.edrive.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordWithTokenRequest {
    private String token;
    private String newPassword;
}
