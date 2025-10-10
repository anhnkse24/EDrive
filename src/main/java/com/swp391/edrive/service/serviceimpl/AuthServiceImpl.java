package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.config.JwtTokenProvider;
import com.swp391.edrive.dto.request.ChangePasswordRequest;
import com.swp391.edrive.dto.request.LoginRequest;
import com.swp391.edrive.dto.request.RegisterRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.UserResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.enums.UserRole;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.repository.TokenRepository;
import com.swp391.edrive.repository.UserRepository;
import com.swp391.edrive.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.swp391.edrive.entity.PasswordResetToken;
import com.swp391.edrive.repository.PasswordResetTokenRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.time.LocalDateTime;
import java.util.UUID;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender mailSender;

    @Override
    public UserResponse register(RegisterRequest request) {
        // Check confirmPassword
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }
        // Check email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        // Check username
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã được sử dụng");
        }
        // Check phone
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }
        // Check dealerName trùng
        dealerRepository.findByDealerName(request.getDealerName())
                .ifPresent(d -> {
                    throw new RuntimeException("Dealer name đã được sử dụng");
                });

        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Tạo dealer mới
        Dealer dealer = new Dealer();
        dealer.setDealerName(request.getDealerName());
        dealer.setAddress(request.getAddress());
        dealer.setContactPerson(request.getFullName());
        dealer.setPhone(request.getPhone());
        dealerRepository.save(dealer);

        // Tạo user mới
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encodedPassword);
        user.setRole(UserRole.DEALER_STAFF); // mặc định staff
        user.setDealer(dealer);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole(),
                savedUser.getDealer().getDealerName()
        );
    }

    @Override
    public LoginResult login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            String accessToken = tokenProvider.generateToken(authentication);

            User u = userRepository.findByUsername(request.getUsername()).orElseThrow();
            RefreshToken refreshToken = refreshTokenServiceImpl.createRefreshToken(u);

            return LoginResult.success(accessToken, refreshToken.getToken());
        } catch (BadCredentialsException e) {
            return LoginResult.fail("Invalid username or password");
        }
    }

    @Override
    public ResponseEntity<ResponseObject> refresh(String refreshTokenStr) {
        var tokenEntity = refreshTokenServiceImpl.findByToken(refreshTokenStr).orElse(null);
        if (tokenEntity == null) {
            return ResponseEntity.status(401)
                    .body(new ResponseObject(401, "Invalid refresh token", null));
        }
        if (refreshTokenServiceImpl.isExpired(tokenEntity)) {
            refreshTokenServiceImpl.delete(tokenEntity);
            return ResponseEntity.status(401)
                    .body(new ResponseObject(401, "Refresh token expired", null));
        }

        UserDetails ud = userDetailsService.loadUserByUsername(tokenEntity.getUser().getUsername());
        Authentication auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
        String newAccess = tokenProvider.generateToken(auth);

        return ResponseEntity.ok(new ResponseObject(200, "Token refreshed", Map.of("token", newAccess)));
    }

    @Transactional
    @Override
    public ResponseEntity<ResponseObject> changePassword(String username, ChangePasswordRequest request) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(400, "Old password is incorrect", null));
            }

            if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(400, "New password cannot be the same as old password", null));
            }

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(400, "Confirm password does not match new password", null));
            }

            String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(encodedNewPassword);
            userRepository.saveAndFlush(user); // flush để chắc chắn update DB ngay

            revokeAllUserTokens(user);

            return ResponseEntity.ok(
                    new ResponseObject(200, "Password changed successfully, please login again", null)
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseObject(500, "Error changing password: " + e.getMessage(), null));
        }
    }

    private void revokeAllUserTokens(User user) {
        var validTokens = tokenRepository.findAllByUser_UserIdAndExpiredFalseAndRevokedFalse(user.getUserId());
        if (validTokens == null || validTokens.isEmpty()) return;

        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }
    @Override
    public ResponseEntity<ResponseObject> requestPasswordReset(String email) {
        try {
            // 🔍 1. Tìm user theo email
            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(400, "Email không tồn tại trong hệ thống", null));
            }

            User user = userOpt.get();

            // 🔑 2. Tạo token reset (hết hạn sau 15 phút)
            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(expiry)
                    .build();
            passwordResetTokenRepository.save(resetToken);

            // 🔗 3. Tạo link reset mật khẩu
            String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;

            // 📧 4. Soạn và gửi email
            try {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setTo(user.getEmail());
                mail.setSubject("Password Reset Request");
                mail.setText("Nhấp vào liên kết sau để đặt lại mật khẩu: " + resetLink
                        + "\nLưu ý: Link sẽ hết hạn sau 15 phút.");
                mailSender.send(mail);

                System.out.println("✅ Đã gửi email reset đến: " + user.getEmail());
            } catch (Exception e) {
                // ⚠️ Nếu không gửi được mail thật, chỉ log lỗi (để test vẫn hoạt động)
                System.err.println("⚠️ Gửi email thất bại: " + e.getMessage());
            }

            // ✅ 5. Trả về token hoặc link reset trong data (để frontend test dễ)
            return ResponseEntity.ok(
                    new ResponseObject(
                            200,
                            "Đã xử lý yêu cầu reset password cho " + email,
                            token
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ResponseObject(500, "Lỗi khi xử lý yêu cầu reset password", null));
        }
    }
    @Override
    public ResponseEntity<ResponseObject> resetPassword(String token, String newPassword) {
        var tokenOpt = passwordResetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject<>(400, "Token không hợp lệ", null));
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject<>(400, "Token đã hết hạn", null));
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
        revokeAllUserTokens(user);

        return ResponseEntity.ok(
                new ResponseObject<>(200, "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.", null)
        );
    }

}
