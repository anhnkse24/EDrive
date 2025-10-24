package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.UpdateProfileRequest;
import com.swp391.edrive.dto.response.ProfileResponse;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.repository.UserRepository;
import com.swp391.edrive.service.ProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public ProfileResponse getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return ProfileResponse.from(user);
    }

    @Override
    @Transactional
    public ProfileResponse updateMyProfile(String username, UpdateProfileRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Cập nhật thông tin có thay đổi
        if (req.getFullName() != null && !req.getFullName().isBlank())
            user.setFullName(req.getFullName());
        if (req.getEmail() != null && !req.getEmail().isBlank())
            user.setEmail(req.getEmail());
        if (req.getPhone() != null && !req.getPhone().isBlank())
            user.setPhone(req.getPhone());

        User saved = userRepository.save(user);
        return ProfileResponse.from(saved);
    }
}
