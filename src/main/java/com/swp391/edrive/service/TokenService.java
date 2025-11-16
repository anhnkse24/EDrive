package com.swp391.edrive.service;

import com.swp391.edrive.entity.User;

public interface TokenService {
    String generateToken(User user);

    User getAccountByToken(String token);

}