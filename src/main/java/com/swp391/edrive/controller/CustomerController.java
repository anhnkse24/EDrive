package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.CustomerRequest;
import com.swp391.edrive.dto.response.CustomerResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dealer/{dealerId}/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "API quản lý khách hàng theo Dealer")
@SecurityRequirements
@SecurityRequirement(name = "api")

public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Thêm khách hàng mới cho Dealer")
    @PostMapping
    public ResponseEntity<ResponseObject<CustomerResponse>> createCustomer(
            @PathVariable Long dealerId,
            @Valid @RequestBody CustomerRequest request) {

        CustomerResponse res = customerService.createCustomer(dealerId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.<CustomerResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Customer created successfully")
                        .data(res)
                        .build()
                );
    }

    @Operation(summary = "Cập nhật khách hàng theo Dealer & ID")
    @PutMapping("/{customerId}")
    public ResponseEntity<ResponseObject<CustomerResponse>> updateCustomer(
            @PathVariable Long dealerId,
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequest request) {

        CustomerResponse res = customerService.updateCustomer(dealerId, customerId, request);

        return ResponseEntity.ok(
                ResponseObject.<CustomerResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Customer updated successfully")
                        .data(res)
                        .build()
        );
    }

    @Operation(summary = "Xóa khách hàng theo Dealer & ID")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ResponseObject<Void>> deleteCustomer(
            @PathVariable Long dealerId,
            @PathVariable Long customerId) {

        customerService.deleteCustomer(dealerId, customerId);

        return ResponseEntity.ok(
                ResponseObject.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Customer deleted successfully")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "Lấy chi tiết khách hàng theo Dealer & ID")
    @GetMapping("/{customerId}")
    public ResponseEntity<ResponseObject<CustomerResponse>> getCustomerById(
            @PathVariable Long dealerId,
            @PathVariable Long customerId) {

        CustomerResponse res = customerService.getCustomerById(dealerId, customerId);

        return ResponseEntity.ok(
                ResponseObject.<CustomerResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Customer retrieved successfully")
                        .data(res)
                        .build()
        );
    }

    @Operation(summary = "Lấy danh sách khách hàng theo Dealer")
    @GetMapping
    public ResponseEntity<ResponseObject<List<CustomerResponse>>> getCustomersByDealer(
            @PathVariable Long dealerId) {

        List<CustomerResponse> res = customerService.getCustomersByDealer(dealerId);

        return ResponseEntity.ok(
                ResponseObject.<List<CustomerResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Customers retrieved successfully")
                        .data(res)
                        .build()
        );
    }
}

