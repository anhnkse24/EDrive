package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ContractApprovalRequest;
import com.swp391.edrive.dto.request.ContractFromOrderRequest;
import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.ContractFileResponse;
import com.swp391.edrive.dto.response.ContractResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.Contract;
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
}
