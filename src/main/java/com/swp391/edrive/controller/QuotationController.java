package com.swp391.edrive.controller;


import com.swp391.edrive.dto.request.QuotationRequest;
import com.swp391.edrive.dto.response.QuotationResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.Quotation;
import com.swp391.edrive.service.QuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class QuotationController {

    private final QuotationService quotationService;


    @Operation(summary = "Tạo báo giá")
    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createQuotation(@RequestBody QuotationRequest quotationRequest) {
        try {
            // Gọi service để tạo báo giá
            QuotationResponse quotationResponse = quotationService.createQuotation(quotationRequest);

            // Trả về ResponseObject đơn giản với mã trạng thái 200 và thông tin báo giá
            ResponseObject response = new ResponseObject(
                    200,
                    "Báo giá được tạo thành công",
                    quotationResponse
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Trả về lỗi nếu có
            ResponseObject errorResponse = new ResponseObject(
                    400,
                    "Có lỗi xảy ra: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
