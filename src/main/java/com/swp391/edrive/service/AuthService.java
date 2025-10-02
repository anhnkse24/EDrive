package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.LoginRequest;
import com.swp391.edrive.dto.request.RegisterRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.UserResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    /** Đăng nhập: trả access + refresh (string) để Controller set cookie */
    LoginResult login(LoginRequest request);

    /** Refresh access token từ refresh token string */
    ResponseEntity<ResponseObject> refresh(String refreshTokenStr);

    /** DTO kết quả login (giữ nguyên như bạn đang dùng) */
    record LoginResult(boolean ok, String accessToken, String refreshToken, String errorMessage) {
        public static LoginResult success(String access, String refresh) {
            return new LoginResult(true, access, refresh, null);
        }
        public static LoginResult fail(String msg) {
            return new LoginResult(false, null, null, msg);
        }
    }
}
