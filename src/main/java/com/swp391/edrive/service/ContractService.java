package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.CreateContractFromQuotationRequest;
import com.swp391.edrive.dto.request.SignContractRequest;
import com.swp391.edrive.dto.request.UpdateContractTermsRequest;
import com.swp391.edrive.dto.response.ContractResponse;
import com.swp391.edrive.enums.ContractStatus;

import java.util.List;

public interface ContractService {
    ContractResponse createFromQuotation(CreateContractFromQuotationRequest req);
    ContractResponse sign(Long contractId, SignContractRequest req);
    ContractResponse complete(Long contractId);   // hoàn tất thực hiện
    ContractResponse terminate(Long contractId, String reason); // chấm dứt sớm
    ContractResponse cancel(Long contractId, String reason);    // huỷ trước khi ký
    ContractResponse updateTerms(Long contractId, UpdateContractTermsRequest req);

    ContractResponse get(Long id);
    List<ContractResponse> getAll();
    List<ContractResponse> getByDealer(Long dealerId);
    List<ContractResponse> getByStatus(ContractStatus status);
}
