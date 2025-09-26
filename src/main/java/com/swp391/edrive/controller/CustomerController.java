package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.CustomerRequest;
import com.swp391.edrive.dto.response.CustomerResponse;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.entity.Customer;
import com.swp391.edrive.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ResponseObject> createCustomer(@RequestBody CustomerRequest request) {
        CustomerResponse res = customerService.createCustomer(request);
        return ResponseEntity.ok(new ResponseObject(200, "Customer created successfully", res));
    }
}
