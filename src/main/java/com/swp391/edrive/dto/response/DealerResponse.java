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
    private String address;
    private String contactPerson;
    private String phone;
    private Integer contractId;
}
