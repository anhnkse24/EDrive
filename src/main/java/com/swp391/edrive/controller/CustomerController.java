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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ResponseObject> createCustomer(
            @PathVariable Long dealerId,
            @Valid @RequestBody CustomerRequest request) {
        var res = customerService.createCustomer(dealerId, request);
        return ResponseEntity.ok(new ResponseObject(200, "Customer created successfully", res));
    }

    @Operation(summary = "Cập nhật khách hàng theo Dealer & ID")
    @PutMapping("/{customerId}")
    public ResponseEntity<ResponseObject> updateCustomer(
            @PathVariable Long dealerId,
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequest request) {
        var res = customerService.updateCustomer(dealerId, customerId, request);
        return ResponseEntity.ok(new ResponseObject(200, "Customer updated successfully", res));
    }

    @Operation(summary = "Xóa khách hàng theo Dealer & ID")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ResponseObject> deleteCustomer(
            @PathVariable Long dealerId,
            @PathVariable Long customerId) {
        customerService.deleteCustomer(dealerId, customerId);
        return ResponseEntity.ok(new ResponseObject(200, "Customer deleted successfully", null));
    }

    @Operation(summary = "Lấy chi tiết khách hàng theo Dealer & ID")
    @GetMapping("/{customerId}")
    public ResponseEntity<ResponseObject> getCustomerById(
            @PathVariable Long dealerId,
            @PathVariable Long customerId) {
        var res = customerService.getCustomerById(dealerId, customerId);
        return ResponseEntity.ok(new ResponseObject(200, "Customer retrieved successfully", res));
    }

    @Operation(summary = "Lấy danh sách khách hàng theo Dealer")
    @GetMapping
    public ResponseEntity<ResponseObject> getCustomersByDealer(@PathVariable Long dealerId) {
        var res = customerService.getCustomersByDealer(dealerId);
        return ResponseEntity.ok(new ResponseObject(200, "Customers retrieved successfully", res));
    }
}

