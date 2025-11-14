package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ContractApprovalRequest;
import com.swp391.edrive.dto.request.ContractFromOrderRequest;
import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.*;
import com.swp391.edrive.entity.Contract;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")

public class ContractController {

    private final ContractService service;

    @PostMapping
    public ResponseEntity<ContractResponse> create(@RequestBody ContractRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @Operation(summary = "Tạo hợp đồng từ Order", description = "Tạo hợp đồng từ đơn hàng đã thanh toán, payment status chuyển sang ĐÃ_CỌC")
    @PostMapping("/from-order")
    public ResponseObject<ContractResponse> createFromOrder(@RequestBody ContractFromOrderRequest req) {
        ContractResponse result = service.createContractFromOrder(req.getOrderId());
        return ResponseObject.<ContractResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Tạo hợp đồng thành công. Trạng thái thanh toán đã chuyển sang ĐÃ_CỌC")
                .data(result)
                .build();
    }

    @Operation(summary = "Admin duyệt hoặc từ chối hợp đồng", description = "Admin approve/reject hợp đồng đang ở trạng thái CHỜ_DUYỆT")
    @PostMapping("/review")
    public ResponseObject<ContractResponse> reviewContract(@RequestBody ContractApprovalRequest req) {
        ContractResponse result = service.reviewContract(
                req.getContractId(),
                req.getApproved(),
                req.getRejectionReason()
        );

        String message = req.getApproved()
                ? "Phê duyệt hợp đồng thành công"
                : "Từ chối hợp đồng thành công";

        return ResponseObject.<ContractResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(result)
                .build();
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<ContractResponse> submit(@PathVariable Long id) {
        return ResponseEntity.ok(service.submitToManufacturer(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ContractResponse> approve(@PathVariable Long id,
                                                    @RequestParam(required = false) String note) {
        return ResponseEntity.ok(service.approve(id, note));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ContractResponse> reject(@PathVariable Long id,
                                                   @RequestParam(required = false) String note) {
        return ResponseEntity.ok(service.reject(id, note));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<List<ContractResponse>> listByDealer(@PathVariable Long dealerId) {
        return ResponseEntity.ok(service.getByDealer(dealerId));
    }

    @GetMapping
    public ResponseObject<List<ContractResponse>> getAllContracts() {
        List<ContractResponse> result = service.getAllContracts();
        return ResponseObject.<List<ContractResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách hợp đồng thành công")
                .data(result)
                .build();
    }

    @PostMapping(
            value = "/{contractId}/upload-pdf",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )    public ResponseEntity<ContractFileResponse> uploadContractPdf(
            @PathVariable Long contractId,
            @RequestParam("file") MultipartFile file) {

        ContractFileResponse response = service.uploadPdf(contractId, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadContract(@PathVariable Long id) {
        Contract contract = service.findEntityById(id);
        if (contract == null || contract.getPdfFilename() == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get("uploads/contracts", contract.getPdfFilename());
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + contract.getPdfFilename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    // ==================== NEW API ENDPOINTS ====================

    /**
     * Get detailed contract information with buyer, dealer, manufacturer details
     * GET /api/contracts/{contractId}
     */
    @Operation(summary = "Get contract detail", description = "Returns detailed contract information including buyer, dealer, manufacturer, and pricing")
    @GetMapping("/{contractId}/detail")
    public ResponseEntity<ContractDetailResponse> getContractDetail(@PathVariable Long contractId) {
        return ResponseEntity.ok(service.getContractDetail(contractId));
    }

    /**
     * Get detailed order information with items and pricing
     * GET /api/orders/{orderId}
     */
    @Operation(summary = "Get order detail", description = "Returns detailed order information with items and money breakdown")
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable String orderId) {
        return ResponseEntity.ok(service.getOrderDetail(orderId));
    }

    /**
     * Save manufacturer signature
     * PUT /api/contracts/{contractId}/sign/manufacturer
     */
    @Operation(summary = "Manufacturer signs contract", description = "Saves manufacturer's signature on the contract")
    @PutMapping("/{contractId}/sign/manufacturer")
    public ResponseEntity<SignatureResponse> saveManufacturerSignature(
            @PathVariable Long contractId,
            @RequestBody java.util.Map<String, String> request) {
        
        String signatureData = request.get("signatureData");
        if (signatureData == null || signatureData.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(service.saveManufacturerSignature(contractId, signatureData));
    }

    /**
     * Upload contract PDF with custom filename
     * POST /api/contracts/{contractId}/upload-pdf-new
     */
    @Operation(summary = "Upload contract PDF", description = "Uploads a PDF file for the contract with optional custom filename")
    @PostMapping(value = "/{contractId}/upload-pdf-new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PdfUploadResponse> uploadContractPdfNew(
            @PathVariable Long contractId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileName", required = false) String fileName) {
        
        return ResponseEntity.ok(service.uploadContractPdf(contractId, file, fileName));
    }

    /**
     * Dealer signs contract (after manufacturer has signed)
     * PUT /api/contracts/{contractId}/sign/dealer
     */
    @Operation(summary = "Dealer signs contract", description = "Saves dealer's signature on the contract. Can only be done after manufacturer has signed (status = SIGNING).")
    @PutMapping("/{contractId}/sign/dealer")
    public ResponseEntity<SignatureResponse> dealerSignContract(
            @PathVariable Long contractId,
            @RequestBody java.util.Map<String, String> request) {
        
        String signatureData = request.get("signatureData");
        if (signatureData == null || signatureData.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        ContractResponse contractResponse = service.dealerSign(contractId, signatureData);
        
        // Build response similar to manufacturer signature
        Contract contract = service.findEntityById(contractId);
        Dealer dealer = contract.getDealer();
        
        SignatureResponse response = SignatureResponse.builder()
                .id(contract.getId())
                .orderId(contract.getOrder() != null ? contract.getOrder().getOrderId() : null)
                .status(contract.getStatus().name())
                .dealer(SignatureResponse.DealerSignature.builder()
                        .name(dealer != null ? dealer.getDealerName() : "Dealer")
                        .signatureData(signatureData)
                        .signedAt(contract.getDealerSignedAt())
                        .build())
                .updatedAt(contract.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
