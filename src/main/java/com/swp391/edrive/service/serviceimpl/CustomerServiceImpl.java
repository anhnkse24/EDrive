package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.CustomerRequest;
import com.swp391.edrive.dto.response.CustomerResponse;
import com.swp391.edrive.entity.Customer;
import com.swp391.edrive.repository.CustomerRepository;
import com.swp391.edrive.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest req) {
        Customer c = new Customer();
        c.setFullName(req.getFullName());
        c.setDob(req.getDob());
        c.setGender(req.getGender());
        c.setEmail(req.getEmail());
        c.setPhone(req.getPhone());
        c.setAddress(req.getAddress());
        c.setIdCardNo(req.getIdCardNo());
        return toResponse(customerRepository.save(c));
    }

    private CustomerResponse toResponse(Customer c) {
        return CustomerResponse.builder()
                .customerId(c.getCustomerId())
                .fullName(c.getFullName())
                .dob(c.getDob())
                .gender(c.getGender())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .idCardNo(c.getIdCardNo())
                .build();
    }
}
