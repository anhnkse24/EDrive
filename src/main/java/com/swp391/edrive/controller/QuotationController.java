package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.QuotationCreateRequest;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.QuotationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
@SecurityRequirement(name = "api") // Nếu API public thì có thể bỏ
public class QuotationController {

    private final QuotationService quotationService;

    @PostMapping("/preview")
    public ResponseEntity<ResponseObject<QuotationResponse>> preview(
            @RequestBody @Valid QuotationCreateRequest req) {
        QuotationResponse res = quotationService.previewQuotation(req);
        return ResponseEntity.ok(new ResponseObject<>(200, "Preview OK", res));
    }

    @PostMapping
    public ResponseEntity<ResponseObject<QuotationResponse>> create(
            @RequestBody @Valid QuotationCreateRequest req) {
        QuotationResponse res = quotationService.createQuotation(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject<>(201, "Quotation created", res));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<QuotationResponse>> get(@PathVariable Long id) {
        QuotationResponse res = quotationService.getQuotation(id);
        return ResponseEntity.ok(new ResponseObject<>(200, "Fetched", res));
    }
}
