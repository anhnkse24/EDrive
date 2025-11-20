package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.UpdateProfileRequest;
import com.swp391.edrive.dto.response.ProfileResponse;
import com.swp391.edrive.entity.Dealer;
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
        return toResponse(user);
    }

    @Override
    @Transactional
    public ProfileResponse updateMyProfile(String username, UpdateProfileRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (req.getFullName() != null && !req.getFullName().isBlank())
            user.setFullName(req.getFullName());
        if (req.getEmail() != null && !req.getEmail().isBlank())
            user.setEmail(req.getEmail());
        if (req.getPhone() != null && !req.getPhone().isBlank())
            user.setPhone(req.getPhone());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    // ====================
    // Helper convert entity
    // ====================
    private ProfileResponse toResponse(User user) {
        Dealer dealer = user.getDealer();

        String fullAddress = null;
        if (dealer != null) {
            fullAddress = String.format("%s, %s, %s, %s",
                    dealer.getHouseNumberAndStreet() != null ? dealer.getHouseNumberAndStreet() : "",
                    dealer.getWardOrCommune() != null ? dealer.getWardOrCommune() : "",
                    dealer.getDistrict() != null ? dealer.getDistrict() : "",
                    dealer.getProvinceOrCity() != null ? dealer.getProvinceOrCity() : ""
            ).replaceAll(", ,", ",").trim();
        }

        return ProfileResponse.builder()
                .profileId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())
                .agencyName(dealer != null ? dealer.getDealerName() : null)
                .contactPerson(dealer != null ? dealer.getContactPerson() : null)
                .streetAddress(dealer != null ? dealer.getHouseNumberAndStreet() : null)
                .ward(dealer != null ? dealer.getWardOrCommune() : null)
                .district(dealer != null ? dealer.getDistrict() : null)
                .city(dealer != null ? dealer.getProvinceOrCity() : null)
                .fullAddress(fullAddress)
                .dealerId(dealer != null ? dealer.getDealerId() : null)
                .build();
    }
}
