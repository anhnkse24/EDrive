package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.config.JwtTokenProvider;
import com.swp391.edrive.dto.request.LoginRequest;
import com.swp391.edrive.dto.request.RegisterRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.UserResponse;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.enums.UserRole;
import com.swp391.edrive.repository.DealerRepository;
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
}
