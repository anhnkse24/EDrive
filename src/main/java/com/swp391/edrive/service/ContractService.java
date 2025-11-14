package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.ContractFileResponse;
import com.swp391.edrive.dto.response.CustomerContractResponse;
import com.swp391.edrive.dto.response.ManufacturerContractResponse;
import com.swp391.edrive.entity.Contract;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContractService {
    // ========== Hãng ↔ Đại lý ==========
    ManufacturerContractResponse create(ContractRequest req);
    ManufacturerContractResponse submitToManufacturer(Long contractId);
    ManufacturerContractResponse approve(Long contractId, String note);
    ManufacturerContractResponse reject(Long contractId, String note);

    // ✅ Trả về Object để có thể là 1 trong 2 loại
    Object getById(Long id);
    Object getAllContracts();

    List<ManufacturerContractResponse> getByDealer(Long dealerId);

    // ========== Đại lý ↔ Khách hàng ==========
    CustomerContractResponse createContractFromOrder(String orderId);
    CustomerContractResponse reviewContract(Long contractId, Boolean approved, String rejectionReason);

    // ========== Common ==========
    ContractFileResponse uploadPdf(Long contractId, MultipartFile file);
    Contract findEntityById(Long id);

}
