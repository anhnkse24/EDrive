package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.UpdateProfileRequest;
import com.swp391.edrive.dto.response.ProfileResponse;
import com.swp391.edrive.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Xem hồ sô")
    public ProfileResponse getProfile(Authentication auth) {
        return profileService.getMyProfile(auth.getName());
    }

    @Operation(summary = "Chỉnh sửa hồ sơ")
    @PutMapping
    public ProfileResponse updateProfile(Authentication auth,
                                         @Valid @RequestBody UpdateProfileRequest req) {
        return profileService.updateMyProfile(auth.getName(), req);
    }
}
