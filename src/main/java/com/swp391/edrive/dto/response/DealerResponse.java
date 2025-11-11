package com.swp391.edrive.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerResponse {
    private Long userId;
    private Long dealerId;
    private String dealerName;
    private  String email;

    private String houseNumberAndStreet;
    private String wardOrCommune;
    private String district;
    private String provinceOrCity;

    private String contactPerson;
    private String phone;
    private Set<String> roles;

}
