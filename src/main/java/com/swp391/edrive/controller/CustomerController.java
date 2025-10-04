package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.CustomerRequest;
import com.swp391.edrive.dto.response.CustomerResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService; // <-- inject interface

    @PostMapping
    public ResponseEntity<ResponseObject> createCustomer(@RequestBody CustomerRequest request) {
        CustomerResponse res = customerService.createCustomer(request);
        return ResponseEntity.ok(new ResponseObject(200, "Customer created successfully", res));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateCustomer(
            @PathVariable("id") Long id,
            @RequestBody CustomerRequest request) {
        CustomerResponse res = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(new ResponseObject(200, "Customer updated successfully", res));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(new ResponseObject(200, "Customer deleted successfully", null));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getCustomerById(@PathVariable("id") Long id) {
        CustomerResponse res = customerService.getCustomerById(id);
        return ResponseEntity.ok(new ResponseObject(200, "Customer retrieved successfully", res));
    }

    @GetMapping
    public ResponseEntity<ResponseObject> getAllCustomers() {
        return ResponseEntity.ok(new ResponseObject(200, "Customers retrieved successfully", customerService.getAllCustomers()));
    }
}
