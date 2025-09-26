package com.swp391.edrive.controller;

import com.swp391.edrive.entity.Customer;
import com.swp391.edrive.entity.RegisterRequest;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
@CrossOrigin(origins = "*")

public class RegisterController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        ResponseEntity<String> response = null;

        User newUser = null;
        try {
            newUser = new User();

            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(registerRequest.getPassword());
            newUser.setFullName(registerRequest.getFullName());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPhone(registerRequest.getPhone());

            User savedUser = userService.createUser(newUser);
            if (savedUser.getUserId() > 0) {
                response = ResponseEntity.status(HttpStatus.CREATED)
                        .body("User registered successfully for user " + newUser.getUsername());
            }

        } catch (Exception exception) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("User registration failed for user " + newUser.getUsername() + " with exeption = " + exception);
        }
        return response;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
