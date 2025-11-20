package com.swp391.edrive.service;

import com.swp391.edrive.dto.request.DiscountPolicyRequest;
import com.swp391.edrive.dto.response.DiscountPolicyResponse;

import java.util.List;

public interface DiscountPolicyService {
    DiscountPolicyResponse createDiscountPolicy(DiscountPolicyRequest request);
    DiscountPolicyResponse updateDiscountPolicy(Long id, DiscountPolicyRequest request);
    void deleteDiscountPolicy(Long id);
    DiscountPolicyResponse getDiscountPolicyById(Long id);
    List<DiscountPolicyResponse> getAllDiscountPolicies();
    List<DiscountPolicyResponse> getActiveDiscountPolicies();
}

