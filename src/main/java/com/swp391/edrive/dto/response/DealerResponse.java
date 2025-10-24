package com.swp391.edrive.dto.response;

import com.swp391.edrive.entity.Dealer;
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

    // Getter tự tạo địa chỉ đầy đủ
    public String getFullAddress() {
        return String.format("%s, %s, %s, %s",
                houseNumberAndStreet != null ? houseNumberAndStreet : "",
                wardOrCommune != null ? wardOrCommune : "",
                district != null ? district : "",
                provinceOrCity != null ? provinceOrCity : "");
    }

    // ⚙️ Factory method để tạo DealerResponse từ entity Dealer
    public static DealerResponse from(Dealer dealer) {
        if (dealer == null) return null;

        return DealerResponse.builder()
                .dealerId(dealer.getDealerId())
                .dealerName(dealer.getDealerName())
                .houseNumberAndStreet(dealer.getHouseNumberAndStreet())
                .wardOrCommune(dealer.getWardOrCommune())
                .district(dealer.getDistrict())
                .provinceOrCity(dealer.getProvinceOrCity())
                .contactPerson(dealer.getContactPerson())
                .phone(dealer.getPhone())
                .build();
    }
}
