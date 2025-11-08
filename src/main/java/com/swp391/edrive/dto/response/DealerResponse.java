package com.swp391.edrive.dto.response;

import com.swp391.edrive.entity.Dealer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class DealerResponse {
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
