package com.swp391.edrive.mapper;

import com.swp391.edrive.dto.response.CustomerResponse;
import com.swp391.edrive.dto.response.DealerResponse;
import com.swp391.edrive.dto.response.UserResponse;
import com.swp391.edrive.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {
    public static UserResponse toResponse(User user, String token, String refreshToken) {
        return UserResponse.builder().token(token).refreshToken(refreshToken).build();
    }
    public DealerResponse toUserResponse(User user) {
        return DealerResponse.builder()
                .dealerId(user.getUserId())
                .dealerName(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()))
                .build();
    }
}
