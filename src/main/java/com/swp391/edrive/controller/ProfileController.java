package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.UpdateProfileRequest;
import com.swp391.edrive.dto.response.ProfileResponse;
import com.swp391.edrive.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Quản lý thông tin hồ sơ người dùng")
@SecurityRequirement(name = "api")

public class ProfileController {

    private final ProfileService profileService;

    /**
     * 📘 Xem hồ sơ của người dùng hiện tại
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy hồ sơ của người dùng hiện tại")
    public ResponseEntity<ProfileResponse> getProfile(Authentication auth) {
        ProfileResponse response = profileService.getMyProfile(auth.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * ✏️ Cập nhật hồ sơ cá nhân
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cập nhật hồ sơ người dùng hiện tại")
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication auth,
            @Valid @RequestBody UpdateProfileRequest req
    ) {
        ProfileResponse response = profileService.updateMyProfile(auth.getName(), req);
        return ResponseEntity.ok(response);
    }
}
