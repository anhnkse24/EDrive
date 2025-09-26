package com.swp391.edrive.service;

import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.repository.RefreshTokenRepository;
import com.swp391.edrive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    public RefreshToken createRefreshToken(User user) {
        // tuỳ bạn: mỗi user chỉ 1 refresh token
        refreshTokenRepository.deleteByUser(user);

        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        return refreshTokenRepository.save(rt);
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void delete(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }
}
