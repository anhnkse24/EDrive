package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.repository.RefreshTokenRepository;
import com.swp391.edrive.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration-ms:604800000}") // 7 ngày mặc định
    private long refreshExpirationMs;

    @Override
    public RefreshToken createRefreshToken(User user) {
        // Nếu policy là: mỗi user chỉ 1 refresh token
        refreshTokenRepository.deleteByUser(user);

        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        return refreshTokenRepository.save(rt);
    }

    @Override
    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public void delete(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }
}
