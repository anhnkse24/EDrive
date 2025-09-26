package com.swp391.edrive.controller;

import com.swp391.edrive.config.JwtTokenProvider;
import com.swp391.edrive.dto.request.LoginRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.repository.RefreshTokenRepository;
import com.swp391.edrive.repository.UserRepository;
import com.swp391.edrive.service.AuthService;
import com.swp391.edrive.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@RequestBody LoginRequest request,
                                                HttpServletResponse response) {
        var res = authService.login(request);
        if (!res.ok()) {
            return ResponseEntity.status(401)
                    .body(new ResponseObject(401, res.errorMessage(), null));
        }

        // Đặt refresh token vào cookie HttpOnly (không đưa vào body)
        ResponseCookie cookie = ResponseCookie.from("refresh_token", res.refreshToken())
                .httpOnly(true)
                .secure(false)               // Đặt true khi chạy HTTPS
                .path("/api/auth")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(7))  // khớp refresh-expiration-ms
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // Body chỉ có access token theo yêu cầu
        return ResponseEntity.ok(
                new ResponseObject(200, "Login successful", Map.of("token", res.accessToken()))
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseObject> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401)
                    .body(new ResponseObject(401, "Missing refresh token", null));
        }
        return authService.refresh(refreshToken);
    }
}
