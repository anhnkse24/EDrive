package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.request.RegisterRequest;
import com.swp391.edrive.dto.response.UserResponse;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
@CrossOrigin(origins = "*")

public class RegisterController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<ResponseObject<UserResponse>> registerUser(@Valid @RequestBody RegisterRequest request) {

        try {
            UserResponse response = userService.createUser(request);

            return ResponseEntity.ok(
                    new ResponseObject<>(201,
                            "User registered successfully",
                            response)
            );

        }catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject<>(400,
                            "User registration failed: " + exception.getMessage(),
                            null)
            );
        }
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject<>(400, errorMessage, null));
    }


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
