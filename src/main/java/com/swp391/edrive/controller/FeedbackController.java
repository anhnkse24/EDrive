package com.swp391.edrive.controller;

import com.swp391.edrive.dto.response.FeedbackResponse;
import com.swp391.edrive.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class FeedbackController {

    private final FeedbackService feedbackService;

    // GET /api/feedbacks?page=0&size=10
    @GetMapping
    public ResponseEntity<Page<FeedbackResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(feedbackService.getAll(page, size));
    }

    // GET /api/feedbacks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getById(id));
    }

    // GET /api/feedbacks/by-customer/{customerId}?page=0&size=10
    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<Page<FeedbackResponse>> getByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(feedbackService.getByCustomerId(customerId, page, size));
    }

    // DELETE /api/feedbacks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedbackService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

