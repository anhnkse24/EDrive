package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.request.RegisterRequest;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
@CrossOrigin(origins = "*")

public class RegisterController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<ResponseObject<User>> registerUser(@Valid @RequestBody RegisterRequest request) {
        ResponseEntity<String> response = null;

        User savedUser = null;
        try {
            savedUser = userService.createUser(request);
            return ResponseEntity.ok(
                    new ResponseObject<>(201,
                            "User registered successfully",
                            savedUser)
            );

        }catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject<>(400,
                            "User registration failed: " + exception.getMessage(),
                            null)
            );
        }
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
