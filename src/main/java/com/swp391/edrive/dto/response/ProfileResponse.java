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

}
