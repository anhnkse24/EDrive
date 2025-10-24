package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.CustomerRequest;
import com.swp391.edrive.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(Long dealerId, CustomerRequest request);
    CustomerResponse updateCustomer(Long dealerId, Long customerId, CustomerRequest request);
    void deleteCustomer(Long dealerId, Long customerId);
    CustomerResponse getCustomerById(Long dealerId, Long customerId);
    List<CustomerResponse> getCustomersByDealer(Long dealerId);
}