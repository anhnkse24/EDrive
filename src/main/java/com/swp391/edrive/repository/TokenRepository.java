package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllByUser_UserIdAndExpiredFalseAndRevokedFalse(Long userId);
    Optional<Token> findByToken(String token);
    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokenByUser(@Param("userId") Long userId);
}