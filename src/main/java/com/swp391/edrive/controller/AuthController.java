package com.swp391.edrive.controller;

import com.swp391.edrive.config.JwtTokenProvider;
import com.swp391.edrive.dto.request.LoginRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );

            String token = tokenProvider.generateToken(authentication);

            // Có thể kiểm tra user tồn tại để chắc chắn
            User u = userRepository.findByUsername(request.getUsername()).orElseThrow();

            Map<String, String> data = new HashMap<>();
            data.put("token", token);

            return ResponseEntity.ok(
                    new ResponseObject(200, "Login successful", data)
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(new ResponseObject(401, "Invalid username or password", null));
        }
    }
}
