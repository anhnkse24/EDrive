package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.dto.response.UnverifiedUserResponse;
import com.swp391.edrive.exception.exceptions.BadRequestException;
import com.swp391.edrive.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "api")
public class AdminController {

    private final AuthenticationService authenticationService;

    @GetMapping("/unverified-accounts")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all unverified accounts", 
               description = "Retrieve a list of all user accounts that have not been verified yet")
    public ResponseEntity<ResponseObject<List<UnverifiedUserResponse>>> getAllUnverifiedAccounts() {
        try {
            List<UnverifiedUserResponse> unverifiedAccounts = authenticationService.getAllUnverifiedAccounts();
            return ResponseEntity.ok()
                    .body(new ResponseObject<>(
                            HttpStatus.OK.value(),
                            "Retrieved unverified accounts successfully",
                            unverifiedAccounts));
        } catch (Exception e) {
            throw new BadRequestException("Failed to retrieve unverified accounts: " + e.getMessage(), e);
        }
    }

    @PostMapping("/verify-account/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify account by ID", 
               description = "Manually verify a user account by providing the user ID. This will enable the account and send a confirmation email to the user.")
    public ResponseEntity<ResponseObject<Void>> verifyAccountById(@PathVariable Long userId) {
        try {
            authenticationService.verifyAccountById(userId);
            return ResponseEntity.ok()
                    .body(new ResponseObject<>(
                            HttpStatus.OK.value(),
                            "Account verified successfully",
                            null));
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Failed to verify account: " + e.getMessage(), e);
        }
    }
}
