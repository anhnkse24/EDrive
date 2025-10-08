package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllByUser_UserIdAndExpiredFalseAndRevokedFalse(Long userId);
}