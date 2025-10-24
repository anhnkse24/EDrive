package com.swp391.edrive.dto.response;

import com.swp391.edrive.entity.User;
import com.swp391.edrive.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;

    private DealerResponse dealer; // 🔹 Thêm object DealerResponse

    public static ProfileResponse from(User u) {
        ProfileResponse p = new ProfileResponse();
        p.setId(u.getUserId());
        p.setUsername(u.getUsername());
        p.setFullName(u.getFullName());
        p.setEmail(u.getEmail());
        p.setPhone(u.getPhone());
        p.setRole(u.getRole());

        if (u.getDealer() != null) {
            DealerResponse dealerResponse = DealerResponse.builder()
                    .dealerId(u.getDealer().getDealerId())
                    .dealerName(u.getDealer().getDealerName())
                    .houseNumberAndStreet(u.getDealer().getHouseNumberAndStreet())
                    .wardOrCommune(u.getDealer().getWardOrCommune())
                    .district(u.getDealer().getDistrict())
                    .provinceOrCity(u.getDealer().getProvinceOrCity())
                    .contactPerson(u.getDealer().getContactPerson())
                    .phone(u.getDealer().getPhone())
                    .build();
            p.setDealer(dealerResponse);
        }

        return p;
    }
}
