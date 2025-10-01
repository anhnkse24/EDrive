package com.swp391.edrive.service;

import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    boolean isExpired(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    void delete(RefreshToken token);
}
