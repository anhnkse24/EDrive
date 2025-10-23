package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DealerResponse {
    private Long dealerId;
    private String dealerName;

    // Địa chỉ chi tiết
    private String houseNumberAndStreet;
    private String wardOrCommune;
    private String district;
    private String provinceOrCity;

    // Thông tin liên hệ
    private String contactPerson;
    private String phone;

    // Địa chỉ đầy đủ (nếu muốn trả thêm)
    public String getFullAddress() {
        return String.format("%s, %s, %s, %s",
                houseNumberAndStreet, wardOrCommune, district, provinceOrCity);
    }
}
