package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.config.JwtTokenProvider;
import com.swp391.edrive.dto.request.LoginRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;
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
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final UserDetailsService userDetailsService;

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
