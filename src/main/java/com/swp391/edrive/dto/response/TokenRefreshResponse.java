package com.swp391.edrive.dto.response;

public class TokenRefreshResponse {
    private String token;
    private String refreshToken;

    public TokenRefreshResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
