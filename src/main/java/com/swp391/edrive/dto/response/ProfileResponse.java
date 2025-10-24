package com.swp391.edrive.dto.response;

import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
    private Long profileId;
    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
    private String agencyName;
    private String contactPerson;
    private String agencyPhone;
    private String streetAddress;
    private String ward;
    private String district;
    private String city;
    private String fullAddress;
    private Long dealerId;

    // ======================
    // Convert from User entity
    // ======================
    public static ProfileResponse from(User user) {
        Dealer dealer = user.getDealer();

        String street = (dealer != null && dealer.getHouseNumberAndStreet() != null)
                ? dealer.getHouseNumberAndStreet() : "";
        String ward = (dealer != null && dealer.getWardOrCommune() != null)
                ? dealer.getWardOrCommune() : "";
        String district = (dealer != null && dealer.getDistrict() != null)
                ? dealer.getDistrict() : "";
        String city = (dealer != null && dealer.getProvinceOrCity() != null)
                ? dealer.getProvinceOrCity() : "";

        String fullAddress = String.join(", ",
                street, ward, district, city).replaceAll("(^, |, $)", "");

        return ProfileResponse.builder()
                .profileId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())

                // Dealer info
                .agencyName(dealer != null ? dealer.getDealerName() : null)
                .contactPerson(dealer != null ? dealer.getContactPerson() : null)
                .agencyPhone(dealer != null ? dealer.getPhone() : null)
                .streetAddress(street)
                .ward(ward)
                .district(district)
                .city(city)
                .fullAddress(fullAddress.isBlank() ? null : fullAddress)
                .dealerId(dealer != null ? dealer.getDealerId() : null)
                .build();
    }
}
