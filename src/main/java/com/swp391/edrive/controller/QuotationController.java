package com.swp391.edrive.controller;


import com.swp391.edrive.dto.request.QuotationRequest;
import com.swp391.edrive.dto.request.QuotationStatusUpdateRequest;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.service.QuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class QuotationController {

    private final QuotationService quotationService;
    private final com.swp391.edrive.service.QuotationPdfService quotationPdfService;


    @Operation(summary = "Tạo báo giá")
    @PostMapping("/create")
    public ResponseEntity<ResponseObject<QuotationResponse>> createQuotation(@RequestBody QuotationRequest quotationRequest) {
        try {
            // Lấy user từ security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            // Gọi service để tạo báo giá
            QuotationResponse quotationResponse = quotationService.createQuotation(quotationRequest, user);

            // Trả về ResponseObject đơn giản với mã trạng thái 200 và thông tin báo giá
            ResponseObject<QuotationResponse> response = new ResponseObject<>(
                    200,
                    "Báo giá được tạo thành công",
                    quotationResponse
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Trả về lỗi nếu có
            ResponseObject<QuotationResponse> errorResponse = new ResponseObject<>(
                    400,
                    "Có lỗi xảy ra: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    @Operation(summary = "Cập nhật trạng thái báo giá", description = "Dealer/Admin cập nhật trạng thái báo giá từ PENDING sang ACCEPTED hoặc REJECTED")
    @PutMapping("/update-status")
    public ResponseEntity<ResponseObject<QuotationResponse>> updateQuotationStatus(@RequestBody QuotationStatusUpdateRequest request) {
        try {
            QuotationResponse quotationResponse = quotationService.updateQuotationStatus(
                    request.getQuotationId(),
                    request.getStatus(),
                    request.getRejectionReason()
            );

            String message = "ACCEPTED".equals(request.getStatus())
                    ? "Chấp nhận báo giá thành công"
                    : "Từ chối báo giá thành công";

            ResponseObject<QuotationResponse> response = new ResponseObject<>(
                    200,
                    message,
                    quotationResponse
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseObject<QuotationResponse> errorResponse = new ResponseObject<>(
                    400,
                    "Có lỗi xảy ra: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @Operation(summary = "Lấy danh sách tất cả báo giá")
    @GetMapping
    public ResponseEntity<ResponseObject<List<QuotationResponse>>> getAll() {
        List<QuotationResponse> quotations = quotationService.getAllQuotations();  // Lấy tất cả báo giá
        return ResponseEntity.ok(new ResponseObject<>(200, "Quotations retrieved successfully", quotations));
    }

    // API Lấy báo giá theo ID
    @Operation(summary = "Lấy báo giá theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<QuotationResponse>> getById(@PathVariable Long id) {
        Optional<QuotationResponse> quotationResponse = quotationService.getQuotationById(id);

        if (quotationResponse.isPresent()) {
            return ResponseEntity.ok(new ResponseObject<>(200, "Quotation retrieved successfully", quotationResponse.get()));
        } else {
            return ResponseEntity.status(404).body(new ResponseObject<>(404, "Quotation not found", null));
        }
    }

    @Operation(summary = "Export báo giá ra PDF", description = "Tải xuống báo giá dưới dạng file PDF")
    @GetMapping("/{quotationId}/export-pdf")
    public ResponseEntity<byte[]> exportQuotationToPdf(@PathVariable Long quotationId) {
        try {
            // Generate PDF
            java.io.ByteArrayOutputStream pdfStream = quotationPdfService.generateQuotationPdf(quotationId);
            byte[] pdfBytes = pdfStream.toByteArray();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                    .filename("Bao-gia-" + quotationId + ".pdf")
                    .build()
            );
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            // Return error as plain text
            byte[] errorBytes = ("Lỗi: " + e.getMessage()).getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<>(errorBytes, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Xem trước PDF trong browser", description = "Hiển thị PDF báo giá trực tiếp trong browser")
    @GetMapping("/{quotationId}/preview-pdf")
    public ResponseEntity<byte[]> previewQuotationPdf(@PathVariable Long quotationId) {
        try {
            // Generate PDF
            java.io.ByteArrayOutputStream pdfStream = quotationPdfService.generateQuotationPdf(quotationId);
            byte[] pdfBytes = pdfStream.toByteArray();

            // Set headers for inline display
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                ContentDisposition.builder("inline")
                    .filename("Bao-gia-" + quotationId + ".pdf")
                    .build()
            );
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            byte[] errorBytes = ("Lỗi: " + e.getMessage()).getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<>(errorBytes, headers, HttpStatus.BAD_REQUEST);
        }
    }
}
