package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.CreateQuotationRequest;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.enums.QuotationStatus;
import com.swp391.edrive.service.QuotationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotation")
@RequiredArgsConstructor
public class QuotationController {
    private final QuotationService quotationService;

    @Operation(summary = "Tạo báo giá (cho dealer hoặc khách hàng)")
    @PostMapping
    public ResponseEntity<ResponseObject> createDraft(@RequestBody CreateQuotationRequest req) {
        try {
            QuotationResponse data = quotationService.createDraft(req);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(201, "Quotation draft created successfully", data));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(500, "An unexpected error occurred", null));
        }
    }

    @Operation(summary = "Lấy tất cả báo giá")
    @GetMapping
    public ResponseEntity<ResponseObject> getAll() {
        List<QuotationResponse> data = quotationService.getAll();
        return ResponseEntity.ok(new ResponseObject(200, "Quotation list retrieved successfully", data));
    }

    @Operation(summary = "Tìm báo giá theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getById(@PathVariable Long id) {
        try {
            QuotationResponse data = quotationService.getById(id);
            return ResponseEntity.ok(new ResponseObject(200, "Quotation found", data));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(404, ex.getMessage(), null));
        }
    }

    @Operation(summary = "Tìm báo giá theo đại lý")
    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<ResponseObject> getByDealer(@PathVariable Long dealerId) {
        List<QuotationResponse> data = quotationService.getByDealer(dealerId);
        if (data.isEmpty()) {
            return ResponseEntity.ok(new ResponseObject(200, "No quotations found for this dealer", data));
        }
        return ResponseEntity.ok(new ResponseObject(200, "Quotations by dealer retrieved successfully", data));
    }

    @Operation(summary = "Tìm báo giá theo trạng thái")
    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseObject> getByStatus(@PathVariable String status) {
        try {
            QuotationStatus st = QuotationStatus.valueOf(status.toUpperCase().trim());
            List<QuotationResponse> data = quotationService.getByStatus(st);
            if (data.isEmpty()) {
                return ResponseEntity.ok(new ResponseObject(200, "No quotations found for this status", data));
            }
            return ResponseEntity.ok(new ResponseObject(200, "Quotations by status retrieved successfully", data));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, "Invalid status. Use DRAFT, SENT, APPROVED, CANCELLED, EXPIRED", null));
        }
    }
}
