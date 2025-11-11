package com.swp391.edrive.repository;

import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
    
    List<User> findByIsVerify(boolean isVerify);

    Optional<User> findByDealer(Dealer dealer);
}
