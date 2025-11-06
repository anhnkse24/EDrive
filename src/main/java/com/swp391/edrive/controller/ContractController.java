package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ContractRequest;
import com.swp391.edrive.dto.response.ContractResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.ContractService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        List<ContractResponse> result = service.getAllContracts(); // Gọi service để lấy danh sách hợp đồng
        return ResponseObject.<List<ContractResponse>>builder()
                .statusCode(HttpStatus.OK.value())  // Mã trạng thái HTTP
                .message("Fetched all contracts successfully")  // Thông báo
                .data(result)  // Dữ liệu là danh sách hợp đồng
                .build();
    }

    @PostMapping(
            value = "/{contractId}/upload-pdf",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )    public ResponseEntity<ContractResponse> uploadContractPdf(
            @PathVariable Long contractId,
            @RequestParam("file") MultipartFile file) {

        ContractResponse response = service.uploadPdf(contractId, file);
        return ResponseEntity.ok(response);
    }

}
