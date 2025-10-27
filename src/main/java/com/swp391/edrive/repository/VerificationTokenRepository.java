package com.swp391.edrive.repository;

import com.swp391.edrive.entity.User;
import com.swp391.edrive.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    
    VerificationToken findByUser(User user);

}

