package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.UpdateProfileRequest;
import com.swp391.edrive.dto.response.ProfileResponse;

public interface ProfileService {
    ProfileResponse getMyProfile(String username);
    ProfileResponse updateMyProfile(String username, UpdateProfileRequest req);
}
