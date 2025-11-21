package com.swp391.edrive.controller;


import com.swp391.edrive.dto.request.QuotationRequest;
import com.swp391.edrive.dto.request.QuotationStatusUpdateRequest;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.User;
import com.swp391.edrive.service.QuotationPdfService;
import com.swp391.edrive.service.QuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final QuotationPdfService quotationPdfService;


    @Operation(summary = "Tạo báo giá")
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
    public ResponseEntity<ResponseObject<QuotationResponse>> createQuotation(
            @RequestBody QuotationRequest quotationRequest) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            QuotationResponse data = quotationService.createQuotation(quotationRequest, user);

            return ResponseEntity.ok(
                    new ResponseObject<>(200, "Báo giá được tạo thành công", data)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject<>(400, "Có lỗi xảy ra: " + e.getMessage(), null)
            );
        }
    }

    @Operation(summary = "Cập nhật trạng thái báo giá")
    @PutMapping("/update-status")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
    public ResponseEntity<ResponseObject<QuotationResponse>> updateQuotationStatus(
            @RequestBody QuotationStatusUpdateRequest request) {

        try {
            QuotationResponse data = quotationService.updateQuotationStatus(
                    request.getQuotationId(),
                    request.getStatus(),
                    request.getRejectionReason()
            );

            String message = request.getStatus().equals("ACCEPTED")
                    ? "Chấp nhận báo giá thành công"
                    : "Từ chối báo giá thành công";

            return ResponseEntity.ok(new ResponseObject<>(200, message, data));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject<>(400, "Có lỗi xảy ra: " + e.getMessage(), null)
            );
        }
    }


    @Operation(summary = "Lấy danh sách tất cả báo giá")
    @GetMapping
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
    public ResponseEntity<ResponseObject<List<QuotationResponse>>> getAll() {
        List<QuotationResponse> list = quotationService.getAllQuotations();
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Quotations retrieved successfully", list)
        );
    }

    // API Lấy báo giá theo ID
    @Operation(summary = "Lấy báo giá theo ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
    public ResponseEntity<ResponseObject<QuotationResponse>> getById(@PathVariable Long id) {

        Optional<QuotationResponse> res = quotationService.getQuotationById(id);

        return res.map(quotation ->
                ResponseEntity.ok(
                        new ResponseObject<>(200, "Quotation retrieved successfully", quotation)
                )
        ).orElseGet(() ->
                ResponseEntity.status(404).body(
                        new ResponseObject<>(404, "Quotation not found", null)
                )
        );
    }

    @Operation(summary = "Export báo giá ra PDF", description = "Tải xuống báo giá dưới dạng file PDF")
    @GetMapping("/{quotationId}/export-pdf")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
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
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
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

    @Operation(summary = "Gửi email báo giá cho khách hàng",
               description = "Gửi email kèm PDF báo giá cho khách hàng. Chỉ áp dụng cho báo giá đã được đại lý duyệt (ACCEPTED)")
    @PostMapping("/{quotationId}/send-email")
    @PreAuthorize("hasAnyRole('DEALER_MANAGER', 'DEALER_STAFF')")
    public ResponseEntity<ResponseObject<String>> sendQuotationEmail(@PathVariable Long quotationId) {
        try {
            quotationService.sendQuotationEmailToCustomer(quotationId);

            return ResponseEntity.ok(
                    new ResponseObject<>(200, "Gửi email báo giá thành công",
                            "Email đã được gửi đến khách hàng kèm file PDF báo giá")
            );
        } catch (IllegalStateException e) {
            // Trường hợp quotation chưa được duyệt
            return ResponseEntity.badRequest().body(
                    new ResponseObject<>(400, e.getMessage(), null)
            );
        } catch (RuntimeException e) {
            // Các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject<>(500, "Lỗi khi gửi email: " + e.getMessage(), null)
            );
        }
    }
}
