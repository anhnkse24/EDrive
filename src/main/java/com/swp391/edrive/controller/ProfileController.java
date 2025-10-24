package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.UpdateProfileRequest;
import com.swp391.edrive.dto.response.ProfileResponse;
import com.swp391.edrive.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Quản lý thông tin hồ sơ người dùng")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 📘 Xem hồ sơ của người dùng hiện tại
     */
    @GetMapping
    @Operation(summary = "Xem hồ sơ người dùng hiện tại")
    public ProfileResponse getProfile(Authentication auth) {
        return profileService.getMyProfile(auth.getName());
    }

    /**
     * ✏️ Cập nhật hồ sơ cá nhân
     */
    @PutMapping
    @Operation(summary = "Cập nhật hồ sơ người dùng hiện tại")
    public ProfileResponse updateProfile(
            Authentication auth,
            @Valid @RequestBody UpdateProfileRequest req
    ) {
        return profileService.updateMyProfile(auth.getName(), req);
    }
}
