package com.swp391.edrive.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class TokenRefreshRequest {

    @NotBlank(message = "refresh token không được để trống")
    private String refreshToken;
}
