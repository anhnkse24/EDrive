package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.CancelContractRequest;
import com.swp391.edrive.dto.request.CreateContractFromQuotationRequest;
import com.swp391.edrive.dto.request.SignContractRequest;
import com.swp391.edrive.dto.request.UpdateContractTermsRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.enums.ContractStatus;
import com.swp391.edrive.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @Operation(summary = "Tạo hợp đồng từ Quotation đã APPROVED")
    @PostMapping("/from-quotation")
    public ResponseEntity<ResponseObject> createFromQuotation(@RequestBody CreateContractFromQuotationRequest req) {
        try {
            var data = contractService.createFromQuotation(req);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(201, "Contract draft created from quotation", data));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ResponseObject(400, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(500, "An unexpected error occurred", null));
        }
    }

    @Operation(summary = "Ký hợp đồng (DRAFT → ACTIVE)")
    @PostMapping("/{id}/sign")
    public ResponseEntity<ResponseObject> sign(@PathVariable Long id, @RequestBody(required = false) SignContractRequest req) {
        try {
            var data = contractService.sign(id, req == null ? new SignContractRequest() : req);
            return ResponseEntity.ok(new ResponseObject(200, "Contract signed", data));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ResponseObject(400, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Cập nhật điều khoản (chỉ khi chưa kết thúc/huỷ)")
    @PutMapping("/{id}/terms")
    public ResponseEntity<ResponseObject> updateTerms(@PathVariable Long id, @RequestBody UpdateContractTermsRequest req) {
        try {
            var data = contractService.updateTerms(id, req);
            return ResponseEntity.ok(new ResponseObject(200, "Contract terms updated", data));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ResponseObject(400, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Huỷ hợp đồng (chỉ DRAFT)")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ResponseObject> cancel(@PathVariable Long id, @RequestBody(required = false) CancelContractRequest req) {
        try {
            var data = contractService.cancel(id, req == null ? null : req.reason);
            return ResponseEntity.ok(new ResponseObject(200, "Contract cancelled", data));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ResponseObject(400, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Hoàn tất hợp đồng (ACTIVE → COMPLETED)")
    @PostMapping("/{id}/complete")
    public ResponseEntity<ResponseObject> complete(@PathVariable Long id) {
        try {
            var data = contractService.complete(id);
            return ResponseEntity.ok(new ResponseObject(200, "Contract completed", data));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ResponseObject(400, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Chấm dứt hợp đồng (ACTIVE → TERMINATED)")
    @PostMapping("/{id}/terminate")
    public ResponseEntity<ResponseObject> terminate(@PathVariable Long id, @RequestBody(required = false) CancelContractRequest req) {
        try {
            var data = contractService.terminate(id, req == null ? null : req.reason);
            return ResponseEntity.ok(new ResponseObject(200, "Contract terminated", data));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(new ResponseObject(400, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Lấy chi tiết hợp đồng")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> get(@PathVariable Long id) {
        try {
            var data = contractService.get(id);
            return ResponseEntity.ok(new ResponseObject(200, "Contract found", data));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(404, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Lấy danh sách hợp đồng")
    @GetMapping
    public ResponseEntity<ResponseObject> list() {
        var data = contractService.getAll();
        return ResponseEntity.ok(new ResponseObject(200, "Contract list", data));
    }

    @Operation(summary = "Lấy hợp đồng theo đại lý")
    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<ResponseObject> byDealer(@PathVariable Long dealerId) {
        var data = contractService.getByDealer(dealerId);
        return ResponseEntity.ok(new ResponseObject(200, "Contracts by dealer", data));
    }

    @Operation(summary = "Lấy hợp đồng theo trạng thái")
    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseObject> byStatus(@PathVariable String status) {
        try {
            var st = ContractStatus.valueOf(status.toUpperCase().trim());
            var data = contractService.getByStatus(st);
            return ResponseEntity.ok(new ResponseObject(200, "Contracts by status", data));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, "Invalid status. Use DRAFT, ACTIVE, COMPLETED, TERMINATED, CANCELLED", null));
        }
    }
}
