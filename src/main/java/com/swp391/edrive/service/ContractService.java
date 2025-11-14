package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.*;
import com.swp391.edrive.entity.Contract;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContractService {
    ContractResponse create(ContractRequest req);

    // ✅ Trả về Object để có thể là 1 trong 2 loại
    Object getById(Long id);
    Object getAllContracts();

    // Admin duyệt hoặc từ chối hợp đồng
    ContractResponse reviewContract(Long contractId, Boolean approved, String rejectionReason);

    ContractResponse submitToManufacturer(Long contractId);
    ContractResponse approve(Long contractId, String note);
    ContractResponse reject(Long contractId, String note);
    List<ContractResponse> getAllContracts();
    // ========== Đại lý ↔ Khách hàng ==========
    CustomerContractResponse createContractFromOrder(String orderId);
    CustomerContractResponse reviewContract(Long contractId, Boolean approved, String rejectionReason);

    ContractResponse getById(Long id);
    List<ContractResponse> getByDealer(Long dealerId);
    ContractFileResponse uploadPdf(Long contractId, MultipartFile file);
    Contract findEntityById(Long id);

    // Signature methods
    ContractResponse dealerSign(Long contractId, String signatureData);
    ContractResponse manufacturerSign(Long contractId, String signatureData);

    // Payment receipt methods
    ContractFileResponse uploadPaymentReceipt(Long contractId, MultipartFile file);
    ContractResponse verifyPayment(Long contractId, String verifiedBy);
    ContractResponse approveDelivery(Long contractId);

    // New detailed API methods
    ContractDetailResponse getContractDetail(Long contractId);
    OrderDetailResponse getOrderDetail(String orderId);
    SignatureResponse saveManufacturerSignature(Long contractId, String signatureData);
    PdfUploadResponse uploadContractPdf(Long contractId, MultipartFile file, String fileName);
}
