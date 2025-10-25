package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.repository.RefreshTokenRepository;
import com.swp391.edrive.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
//        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshTokenOpt = refreshTokenRepository.findByUser(user);
        if (Objects.nonNull(refreshTokenOpt)) {
            refreshTokenOpt.setToken(UUID.randomUUID().toString());
            refreshTokenOpt.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
            return refreshTokenRepository.save(refreshTokenOpt);
        }
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
