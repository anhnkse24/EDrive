package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.FeedbackResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "Get all feedback")
    @GetMapping
    public ResponseEntity<ResponseObject<Page<FeedbackResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FeedbackResponse> data = feedbackService.getAll(page, size);

        return ResponseEntity.ok(
                ResponseObject.<Page<FeedbackResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Feedback list retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @Operation(summary = "Get feedback by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<FeedbackResponse>> getById(@PathVariable Long id) {
        FeedbackResponse data = feedbackService.getById(id);

        return ResponseEntity.ok(
                ResponseObject.<FeedbackResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Feedback retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @Operation(summary = "Get feedback by customer ID")
    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<ResponseObject<Page<FeedbackResponse>>> getByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FeedbackResponse> data = feedbackService.getByCustomerId(customerId, page, size);

        return ResponseEntity.ok(
                ResponseObject.<Page<FeedbackResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Feedback by customer retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @Operation(summary = "Get feedback by dealer ID")
    @GetMapping("/by-dealer/{dealerId}")
    public ResponseEntity<ResponseObject<Page<FeedbackResponse>>> getByDealerId(
            @PathVariable Long dealerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FeedbackResponse> data = feedbackService.getByDealerId(dealerId, page, size);

        return ResponseEntity.ok(
                ResponseObject.<Page<FeedbackResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Feedback by dealer retrieved successfully")
                        .data(data)
                        .build()
        );
    }

    @Operation(summary = "Delete feedback")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> delete(@PathVariable Long id) {
        feedbackService.deleteById(id);

        return ResponseEntity.ok(
                ResponseObject.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Feedback deleted successfully")
                        .data(null)
                        .build()
        );
    }

}

