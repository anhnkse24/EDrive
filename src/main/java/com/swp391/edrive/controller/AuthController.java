package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ChangePasswordRequest;
import com.swp391.edrive.dto.request.LoginRequest;
import com.swp391.edrive.dto.request.RegisterRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.UserResponse;
import com.swp391.edrive.service.serviceimpl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Auth", description = "API quản lý người dùng")
public class AuthController {
    private final AuthServiceImpl authService;
    @Operation(summary = "Đăng kí người dùng mới")
    @PostMapping("/register")
    public ResponseEntity<ResponseObject<UserResponse>> registerUser(
            @Valid @RequestBody RegisterRequest request) {
        try {
            UserResponse response = authService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject<>(201,
                            "User registered successfully",
                            response)
            );

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject<>(400,
                            "User registration failed: " + exception.getMessage(),
                            null)
            );
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject<>(400, errorMessage, null));
    }



    @Operation(summary = "Đăng nhập người dùng")
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

    @Operation(summary = "Làm mới token")
    @PostMapping("/refresh")
    public ResponseEntity<ResponseObject> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401)
                    .body(new ResponseObject(401, "Missing refresh token", null));
        }
        return authService.refresh(refreshToken);
    }

    @Operation(summary = "Đổi mật khẩu người dùng hiện tại")
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> changePassword(@RequestBody ChangePasswordRequest request) {
        return authService.changePassword(request);
    }
    @Operation(summary = "Gửi email reset mật khẩu khi quên mật khẩu")
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseObject> requestPasswordReset(@RequestParam String email) {
        return authService.requestPasswordReset(email);
    }

    @Operation(summary = "Đặt lại mật khẩu qua token reset")
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseObject> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword
    ) {
        return authService.resetPassword(token, newPassword);
    }
}
