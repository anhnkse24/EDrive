package com.swp391.edrive.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
