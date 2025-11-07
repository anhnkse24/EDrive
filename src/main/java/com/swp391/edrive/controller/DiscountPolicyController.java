package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.DiscountPolicyRequest;
import com.swp391.edrive.dto.response.DiscountPolicyResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.DiscountPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/discount-policies")
@RequiredArgsConstructor
@Tag(name = "Discount Policy Management", description = "APIs for managing discount policies (Admin only)")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "api")
public class DiscountPolicyController {

    private final DiscountPolicyService discountPolicyService;

    @Operation(summary = "Create new discount policy", description = "Admin creates a new discount policy for dealer orders")
    @PostMapping
    public ResponseEntity<ResponseObject<DiscountPolicyResponse>> createDiscountPolicy(@Valid @RequestBody DiscountPolicyRequest request) {
        DiscountPolicyResponse response = discountPolicyService.createDiscountPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.<DiscountPolicyResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Discount policy created successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Update discount policy", description = "Admin updates an existing discount policy")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DiscountPolicyResponse>> updateDiscountPolicy(
            @PathVariable Long id,
            @Valid @RequestBody DiscountPolicyRequest request) {
        DiscountPolicyResponse response = discountPolicyService.updateDiscountPolicy(id, request);
        return ResponseEntity.ok(
                ResponseObject.<DiscountPolicyResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Discount policy updated successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Delete discount policy", description = "Admin deletes a discount policy")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> deleteDiscountPolicy(@PathVariable Long id) {
        discountPolicyService.deleteDiscountPolicy(id);
        return ResponseEntity.ok(
                ResponseObject.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Discount policy deleted successfully")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Get discount policy by ID", description = "Retrieve a specific discount policy by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DiscountPolicyResponse>> getDiscountPolicyById(@PathVariable Long id) {
        DiscountPolicyResponse response = discountPolicyService.getDiscountPolicyById(id);
        return ResponseEntity.ok(
                ResponseObject.<DiscountPolicyResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Discount policy retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get all discount policies", description = "Retrieve all discount policies")
    @GetMapping
    public ResponseEntity<ResponseObject<List<DiscountPolicyResponse>>> getAllDiscountPolicies() {
        List<DiscountPolicyResponse> responses = discountPolicyService.getAllDiscountPolicies();
        return ResponseEntity.ok(
                ResponseObject.<List<DiscountPolicyResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("All discount policies retrieved successfully")
                        .data(responses)
                        .build()
        );
    }

    @Operation(summary = "Get active discount policies", description = "Retrieve only active discount policies")
    @GetMapping("/active")
    public ResponseEntity<ResponseObject<List<DiscountPolicyResponse>>> getActiveDiscountPolicies() {
        List<DiscountPolicyResponse> responses = discountPolicyService.getActiveDiscountPolicies();
        return ResponseEntity.ok(
                ResponseObject.<List<DiscountPolicyResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Active discount policies retrieved successfully")
                        .data(responses)
                        .build()
        );
    }
}

