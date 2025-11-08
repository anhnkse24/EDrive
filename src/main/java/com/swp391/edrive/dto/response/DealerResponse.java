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
    private Long userId;
    private Long dealerId;
    private String dealerName;
    private  String email;

    // Địa chỉ chi tiết
    private String houseNumberAndStreet;
    private String wardOrCommune;
    private String district;
    private String provinceOrCity;

    // Thông tin liên hệ
    private String contactPerson;
    private String phone;
    private Set<String> roles;


}
