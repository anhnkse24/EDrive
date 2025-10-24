package com.swp391.edrive.repository;

import com.swp391.edrive.entity.RefreshToken;
import com.swp391.edrive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken save(RefreshToken refreshToken);

    void delete(RefreshToken token);

    void deleteByUser(User user);

    RefreshToken findByUser(User user);
}
