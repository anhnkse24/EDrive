package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.CustomerRequest;
import com.swp391.edrive.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest req);
    CustomerResponse updateCustomer(Long customerId, CustomerRequest req);
    void deleteCustomer(Long customerId);
    CustomerResponse getCustomerById(Long customerId);

    List<CustomerResponse> getAllCustomers();
    List<CustomerResponse> getCustomersByDealer(Long dealerId);


}
