package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.CustomerRequest;
import com.swp391.edrive.dto.response.CustomerResponse;
import com.swp391.edrive.entity.Customer;
import com.swp391.edrive.entity.Dealer;
import com.swp391.edrive.repository.CustomerRepository;
import com.swp391.edrive.repository.DealerRepository;
import com.swp391.edrive.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final DealerRepository dealerRepository;

    @Override
    @Transactional
    public CustomerResponse createCustomer(Long dealerId, CustomerRequest req) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý với ID = " + dealerId));

        Customer c = new Customer();
        c.setFullName(req.getFullName());
        c.setDob(req.getDob());
        c.setGender(req.getGender());     // enum
        c.setEmail(req.getEmail());
        c.setPhone(req.getPhone());
        c.setAddress(req.getAddress());
        c.setIdCardNo(req.getIdCardNo());
        c.setDealer(dealer);

        return toResponse(customerRepository.save(c));
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long dealerId, Long customerId, CustomerRequest req) {
        Customer c = customerRepository.findByCustomerIdAndDealer_DealerId(customerId, dealerId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy khách hàng có ID = " + customerId + " thuộc đại lý ID = " + dealerId));

        c.setFullName(req.getFullName());
        c.setDob(req.getDob());
        c.setGender(req.getGender());     // enum
        c.setEmail(req.getEmail());
        c.setPhone(req.getPhone());
        c.setAddress(req.getAddress());
        c.setIdCardNo(req.getIdCardNo());

        return toResponse(customerRepository.save(c));
    }

    @Override
    @Transactional
    public void deleteCustomer(Long dealerId, Long customerId) {
        Customer c = customerRepository.findByCustomerIdAndDealer_DealerId(customerId, dealerId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy khách hàng có ID = " + customerId + " thuộc đại lý ID = " + dealerId));
        customerRepository.delete(c);
    }

    @Override
    @Transactional
    public CustomerResponse getCustomerById(Long dealerId, Long customerId) {
        Customer c = customerRepository.findByCustomerIdAndDealer_DealerId(customerId, dealerId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy khách hàng có ID = " + customerId + " thuộc đại lý ID = " + dealerId));
        return toResponse(c);
    }

    @Override
    @Transactional
    public List<CustomerResponse> getCustomersByDealer(Long dealerId) {
        List<Customer> customers = customerRepository.findByDealer_DealerId(dealerId);
        return customers.stream().map(this::toResponse).toList();
    }

    private CustomerResponse toResponse(Customer c) {
        return CustomerResponse.builder()
                .customerId(c.getCustomerId())
                .fullName(c.getFullName())
                .dob(c.getDob())
                .gender(c.getGender())     // enum
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .idCardNo(c.getIdCardNo())
                .dealerId(c.getDealer() != null ? c.getDealer().getDealerId() : null)
                .dealerName(c.getDealer() != null ? c.getDealer().getDealerName() : null)
                .build();
    }
}

