package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.LoginRequest;
import com.swp391.edrive.dto.request.UserRegistrationRequest;
import com.swp391.edrive.dto.response.UnverifiedUserResponse;
import com.swp391.edrive.dto.response.UserResponse;
import com.swp391.edrive.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AuthenticationService extends UserDetailsService {
    User register(UserRegistrationRequest request);

    UserResponse login(LoginRequest loginRequest);

    void createPasswordResetTokenForAccount(User user, String token);

    User validatePasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    void deleteResetToken(String token);

    void verifyAccount(String token);
    
    void verifyDealerAccount(String token);

    void deleteAllResetTokensByUser(User user);

    void resetPasswordWithToken(String token, String newPassword);

    void changeUserPassword(String oldPassword, String newPassword);
    
    List<UnverifiedUserResponse> getAllUnverifiedAccounts();
    
    void verifyAccountById(Long userId);
}
