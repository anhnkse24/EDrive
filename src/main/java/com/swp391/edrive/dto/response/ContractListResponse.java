package com.swp391.edrive.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ContractListResponse {
    private List<ManufacturerContractResponse> manufacturerContracts;
    private List<CustomerContractResponse> customerContracts;
}

