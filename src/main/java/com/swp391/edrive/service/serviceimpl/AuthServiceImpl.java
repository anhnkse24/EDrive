package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.config.JwtTokenProvider;
import com.swp391.edrive.dto.request.ChangePasswordRequest;
import com.swp391.edrive.dto.request.LoginRequest;
import com.swp391.edrive.dto.request.RegisterRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.UserResponse;
import com.swp391.edrive.entity.*;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        // 🔹 Kiểm tra mật khẩu xác nhận
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        // 🔹 Kiểm tra trùng dữ liệu người dùng
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã được sử dụng");
        }
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

        // 🔹 Kiểm tra trùng tên đại lý
        dealerRepository.findByDealerName(request.getDealerName())
                .ifPresent(d -> {
                    throw new RuntimeException("Tên đại lý đã được sử dụng");
                });

        // 🔹 Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 🔹 Tạo dealer mới
        Dealer dealer = new Dealer();
        dealer.setDealerName(request.getDealerName());
        dealer.setHouseNumberAndStreet(request.getHouseNumberAndStreet());
        dealer.setWardOrCommune(request.getWardOrCommune());
        dealer.setDistrict(request.getDistrict());
        dealer.setProvinceOrCity(request.getProvinceOrCity());
        dealer.setContactPerson(request.getFullName());
        dealer.setPhone(request.getPhone());

        Dealer savedDealer = dealerRepository.save(dealer);

        // 🔹 Tạo user mới
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encodedPassword);
        user.setRole(UserRole.DEALER_MANAGER); // người tạo đầu tiên là chủ đại lý
        user.setDealer(savedDealer);

        User savedUser = userRepository.save(user);

        // 🔹 Trả về response
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

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            userRepository.saveAndFlush(user);

            // 🔹 Thu hồi tất cả token cũ trước khi cấp mới
            revokeAllUserTokens(user);

            // 🔹 Sinh access token
            String accessToken = tokenProvider.generateToken(authentication);

            // 🔹 Lưu access token vào DB
            Token savedToken = new Token();
            savedToken.setToken(accessToken);
            savedToken.setUser(user);
            savedToken.setExpired(false);
            savedToken.setRevoked(false);
            tokenRepository.save(savedToken);

            // 🔹 Sinh refresh token như cũ
            RefreshToken refreshToken = refreshTokenServiceImpl.createRefreshToken(user);

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
    public ResponseEntity<ResponseObject> changePassword(ChangePasswordRequest request) {
        try {
            // 🔐 Lấy username từ SecurityContext (JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401)
                        .body(new ResponseObject(401, "Unauthorized", null));
            }

            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ Kiểm tra mật khẩu cũ
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(400, "Old password is incorrect", null));
            }

            // ✅ Không cho trùng mật khẩu
            if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(400, "New password cannot be the same as old password", null));
            }

            // ✅ Kiểm tra xác nhận mật khẩu
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(400, "Confirm password does not match new password", null));
            }

            // ✅ Mã hoá và lưu
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.saveAndFlush(user);

            // ✅ Thu hồi token cũ
            revokeAllUserTokens(user);

            return ResponseEntity.ok(
                    new ResponseObject(200,
                            "Password changed successfully. Please login again with your new password.",
                            Map.of("needReLogin", true))
            );

        } catch (Exception e) {
            e.printStackTrace(); // in log cho dễ theo dõi
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
                            resetLink
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
