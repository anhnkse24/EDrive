package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.ContractResponse;

import java.util.List;

public interface ContractService {
    ContractResponse create(ContractRequest req);
    ContractResponse submitToManufacturer(Long contractId);
    ContractResponse approve(Long contractId, String note);
    ContractResponse reject(Long contractId, String note);

    ContractResponse getById(Long id);
    List<ContractResponse> getByDealer(Long dealerId);
}
