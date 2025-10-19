package com.swp391.edrive.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealerRequest {
    private String dealerName;
    private String address;
    private String contactPerson;
    private String phone;
    private Integer contractId;
}