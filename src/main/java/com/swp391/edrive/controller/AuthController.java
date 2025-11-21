package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.*;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.TokenRefreshResponse;
import com.swp391.edrive.dto.response.UserResponse;
import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.exception.exceptions.*;
import com.swp391.edrive.mapper.UserMapper;
import com.swp391.edrive.repository.RefreshTokenRepository;
import com.swp391.edrive.repository.UserRepository;
import com.swp391.edrive.service.AuthenticationService;
import com.swp391.edrive.service.EmailService;
import com.swp391.edrive.service.RefreshTokenService;
import com.swp391.edrive.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class AuthController {

    private static final String ACCOUNT_LOCKED_MESSAGE = "Account has been locked!";
    private static final String LOGIN_SUCCESSFUL = "Login successful";

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;

    @Value("${frontend.url.base}")
    private String frontendUrl;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Đăng ký đại lý với giấy phép kinh doanh")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseObject> register(
            @Valid @ModelAttribute UserRegistrationRequest request,
            @RequestParam(value = "businessLicense", required = false) MultipartFile businessLicense) {

        // Kiểm tra bắt buộc phải upload ảnh
        if (businessLicense == null || businessLicense.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Vui lòng upload giấy phép kinh doanh",
                            null));
        }

        try {
            User user = authenticationService.register(request, businessLicense);
            return ResponseEntity.ok()
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Đăng ký thành công, vui lòng chờ quản trị viên phê duyệt",
                            userMapper.toUserResponse(user)));
        } catch (ConflictException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseObject> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserResponse userResponse = authenticationService.login(loginRequest);
            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), LOGIN_SUCCESSFUL, userResponse));
        } catch (RuntimeException e) {
            // Fixed: Position literals first in String comparisons
            if (ACCOUNT_LOCKED_MESSAGE.equals(e.getMessage())) {
                // Fixed: Preserve stack trace
                throw new ForbiddenException(e.getMessage(), e);
            }
            // Fixed: Preserve stack trace
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @PostMapping("/refresh-token")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseObject> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        try {
            String requestRefreshToken = request.getRefreshToken();
            return refreshTokenService
                    .findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String token = tokenService.generateToken(user);
                        return ResponseEntity.ok()
                                .body(new ResponseObject(
                                        HttpStatus.OK.value(),
                                        "Token refreshed successfully",
                                        new TokenRefreshResponse(token, requestRefreshToken)));
                    })
                    .orElseThrow(
                            () -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
        } catch (TokenRefreshException e) {
            // Fixed: Preserve stack trace
            throw new ForbiddenException(e.getMessage(), e);
        }
    }

    @PostMapping("/forgot-password")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseObject> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            User user = userRepository
                    .findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Email không tồn tại"));

            // Tạo token reset password
            String token = UUID.randomUUID().toString();

            // Xóa tất cả token cũ của user
            authenticationService.deleteAllResetTokensByUser(user);

            // Tạo token mới
            authenticationService.createPasswordResetTokenForAccount(user, token);

            // Tạo link reset password
            String resetPasswordLink = frontendUrl + "reset-password?token=" + token;

            String emailSubject = "Yêu cầu đặt lại mật khẩu";
            String emailText = "Vui lòng nhấp vào liên kết sau để đặt lại mật khẩu của bạn:\n\n"
                    + resetPasswordLink + "\n\n"
                    + "Liên kết này sẽ hết hạn sau 1 giờ.\n"
                    + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.";

            emailService.sendEmail(request.getEmail(), emailSubject, emailText);

            return ResponseEntity.ok()
                    .body(new ResponseObject(
                            HttpStatus.OK.value(), "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn.", null));
        } catch (UsernameNotFoundException e) {
            // Fixed: Preserve stack trace
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @PostMapping("/reset-password")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseObject> resetPasswordWithToken(@RequestBody ResetPasswordWithTokenRequest request) {
        try {
            authenticationService.resetPasswordWithToken(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), "Đặt lại mật khẩu thành công", null));
        } catch (Exception e) {
            // Fixed: Preserve stack trace
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "api")
    @Transactional
    public ResponseEntity<ResponseObject> logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            user.incrementTokenVersion();
            userRepository.save(user);

            refreshTokenRepository.deleteByUser(user);

            return ResponseEntity.ok().body(new ResponseObject(HttpStatus.OK.value(), "Logout successful", null));
        } catch (Exception e) {
            // Fixed: Preserve stack trace
            throw new InternalServerErrorException("Logout failed: " + e.getMessage(), e);
        }
    }



    @PostMapping("/verify")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseObject> verifyAccount(@RequestParam String token) {
        try {
            authenticationService.verifyAccount(token);
            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), "Xác thực tài khoản thành công", null));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }

    @GetMapping("/verify-dealer")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject> verifyDealerAccount(@RequestParam String token) {
        try {
            authenticationService.verifyDealerAccount(token);
            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), "Tài khoản đại lý đã được phê duyệt thành công!", null));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }


    @PostMapping("/change-password")
    @SecurityRequirement(name = "api")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            authenticationService.changeUserPassword(request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), "Password changed successfully", null));
        } catch (UsernameNotFoundException e) {
            // Fixed: Preserve stack trace
            throw new NotFoundException("User not found", e);
        } catch (BadRequestException e) {
            // Fixed: Preserve stack trace
            throw new BadRequestException(e.getMessage(), e);
        } catch (Exception e) {
            // Fixed: Preserve stack trace
            throw new InternalServerErrorException("Failed to change password: " + e.getMessage(), e);
        }
    }

}
