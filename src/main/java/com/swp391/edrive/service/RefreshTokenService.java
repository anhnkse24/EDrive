package com.swp391.edrive.service;

import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken token);
}
