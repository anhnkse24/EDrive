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
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toSet()))
                .dealerId(user.getDealer() != null ? user.getDealer().getDealerId() : null)
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public DealerResponse toUserResponse(User user) {
        DealerResponse.DealerResponseBuilder builder = DealerResponse.builder()
                .userId(user.getUserId())
                .dealerEmail(user.getEmail())
                .contactPhone(user.getPhone())
                .roles(user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()));

        // Map thông tin dealer nếu có
        if (user.getDealer() != null) {
            builder.dealerId(user.getDealer().getDealerId())
                   .dealerName(user.getDealer().getDealerName())
                   .houseNumberAndStreet(user.getDealer().getHouseNumberAndStreet())
                   .wardOrCommune(user.getDealer().getWardOrCommune())
                   .district(user.getDealer().getDistrict())
                   .provinceOrCity(user.getDealer().getProvinceOrCity())
                   .contactPerson(user.getDealer().getContactPerson());
        }

        return builder.build();
    }
}
