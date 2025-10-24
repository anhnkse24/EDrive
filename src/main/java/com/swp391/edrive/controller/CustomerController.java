package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.CustomerRequest;
import com.swp391.edrive.dto.response.CustomerResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "API quản lý danh sách khách hàng")
public class CustomerController {
    private final CustomerService customerService;

    @Operation(summary = "Thêm khách hàng mới")
    @PostMapping
    public ResponseEntity<ResponseObject> createCustomer(@RequestBody CustomerRequest request) {
        CustomerResponse res = customerService.createCustomer(request);
        return ResponseEntity.ok(new ResponseObject(200, "Customer created successfully", res));
    }
    @Operation(summary = "Chỉnh sửa khách hàng theo ID")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateCustomer(
            @PathVariable("id") Long id,
            @RequestBody CustomerRequest request) {
        CustomerResponse res = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(new ResponseObject(200, "Customer updated successfully", res));
    }
    @Operation(summary = "Xóa khách hàng theo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(new ResponseObject(200, "Customer deleted successfully", null));
    }
    @Operation(summary = "Tìm khách hàng theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getCustomerById(@PathVariable("id") Long id) {
        CustomerResponse res = customerService.getCustomerById(id);
        return ResponseEntity.ok(new ResponseObject(200, "Customer retrieved successfully", res));
    }

    @Operation(summary = "Lấy danh sách tất cả khách hàng")
    @GetMapping
    public ResponseEntity<ResponseObject> getAllCustomers() {
        return ResponseEntity.ok(new ResponseObject(200, "Customers retrieved successfully", customerService.getAllCustomers()));
    }
    @Operation(summary = "Lấy danh sách khách hàng theo Dealer ID")
    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<ResponseObject> getCustomersByDealer(@PathVariable("dealerId") Long dealerId) {
        return ResponseEntity.ok(
                new ResponseObject(
                        200,
                        "Customers retrieved successfully for dealer " + dealerId,
                        customerService.getCustomersByDealer(dealerId)
                )
        );
    }
}
