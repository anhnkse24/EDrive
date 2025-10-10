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
    private Long dealerId;
    private String dealerName;

    public static ProfileResponse from(User u) {
        ProfileResponse p = new ProfileResponse();
        p.setId(u.getUserId());
        p.setUsername(u.getUsername());
        p.setFullName(u.getFullName());
        p.setEmail(u.getEmail());
        p.setPhone(u.getPhone());
        p.setRole(u.getRole());
        if (u.getDealer() != null) {
            p.setDealerId(u.getDealer().getDealerId()   );
            p.setDealerName(u.getDealer().getDealerName());
        }
        return p;
    }
}
