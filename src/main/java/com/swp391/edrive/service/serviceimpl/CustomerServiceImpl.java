package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.CustomerRequest;
import com.swp391.edrive.dto.response.CustomerResponse;
import com.swp391.edrive.entity.Customer;
import com.swp391.edrive.repository.CustomerRepository;
import com.swp391.edrive.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long customerId, CustomerRequest req) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id = " + customerId));

        c.setFullName(req.getFullName());
        c.setDob(req.getDob());
        c.setGender(req.getGender());
        c.setEmail(req.getEmail());
        c.setPhone(req.getPhone());
        c.setAddress(req.getAddress());
        c.setIdCardNo(req.getIdCardNo());

        return toResponse(customerRepository.save(c));
    }
    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id = " + customerId));
        customerRepository.delete(c);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long customerId) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id = " + customerId));
        return toResponse(c);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
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
