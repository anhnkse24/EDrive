package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.request.DiscountPolicyRequest;
import com.swp391.edrive.dto.response.DiscountPolicyResponse;
import com.swp391.edrive.entity.DiscountPolicy;
import com.swp391.edrive.exception.exceptions.NotFoundException;
import com.swp391.edrive.exception.exceptions.BadRequestException;
import com.swp391.edrive.repository.DiscountPolicyRepository;
import com.swp391.edrive.service.DiscountPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountPolicyServiceImpl implements DiscountPolicyService {

    private final DiscountPolicyRepository discountPolicyRepository;

    @Override
    @Transactional
    public DiscountPolicyResponse createDiscountPolicy(DiscountPolicyRequest request) {
        validateQuantityRange(request.getMinQuantity(), request.getMaxQuantity());

        // Kiểm tra xung đột với các chính sách hiện có
        checkForOverlappingPolicies(null, request.getMinQuantity(), request.getMaxQuantity());

        DiscountPolicy policy = DiscountPolicy.builder()
                .minQuantity(request.getMinQuantity())
                .maxQuantity(request.getMaxQuantity())
                .discountRate(request.getDiscountRate())
                .isActive(request.getIsActive())
                .description(request.getDescription())
                .build();

        DiscountPolicy savedPolicy = discountPolicyRepository.save(policy);
        log.info("Created discount policy with ID: {}", savedPolicy.getId());

        return mapToResponse(savedPolicy);
    }

    @Override
    @Transactional
    public DiscountPolicyResponse updateDiscountPolicy(Long id, DiscountPolicyRequest request) {
        DiscountPolicy policy = discountPolicyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount policy not found with ID: " + id));

        validateQuantityRange(request.getMinQuantity(), request.getMaxQuantity());

        // Kiểm tra xung đột với các chính sách khác (trừ chính nó)
        checkForOverlappingPolicies(id, request.getMinQuantity(), request.getMaxQuantity());

        policy.setMinQuantity(request.getMinQuantity());
        policy.setMaxQuantity(request.getMaxQuantity());
        policy.setDiscountRate(request.getDiscountRate());
        policy.setIsActive(request.getIsActive());
        policy.setDescription(request.getDescription());

        DiscountPolicy updatedPolicy = discountPolicyRepository.save(policy);
        log.info("Updated discount policy with ID: {}", updatedPolicy.getId());

        return mapToResponse(updatedPolicy);
    }

    @Override
    @Transactional
    public void deleteDiscountPolicy(Long id) {
        if (!discountPolicyRepository.existsById(id)) {
            throw new NotFoundException("Discount policy not found with ID: " + id);
        }
        discountPolicyRepository.deleteById(id);
        log.info("Deleted discount policy with ID: {}", id);
    }

    @Override
    public DiscountPolicyResponse getDiscountPolicyById(Long id) {
        DiscountPolicy policy = discountPolicyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount policy not found with ID: " + id));
        return mapToResponse(policy);
    }

    @Override
    public List<DiscountPolicyResponse> getAllDiscountPolicies() {
        return discountPolicyRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DiscountPolicyResponse> getActiveDiscountPolicies() {
        return discountPolicyRepository.findAll().stream()
                .filter(DiscountPolicy::getIsActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateQuantityRange(Integer minQuantity, Integer maxQuantity) {
        if (minQuantity > maxQuantity) {
            throw new BadRequestException("Min quantity cannot be greater than max quantity");
        }
    }

    /**
     * Kiểm tra xem có chính sách chiết khấu nào đang active trùng khoảng số lượng không
     * @param excludeId ID của policy cần loại trừ (dùng khi update), null nếu tạo mới
     * @param minQuantity Số lượng tối thiểu cần kiểm tra
     * @param maxQuantity Số lượng tối đa cần kiểm tra
     */
    private void checkForOverlappingPolicies(Long excludeId, Integer minQuantity, Integer maxQuantity) {
        List<DiscountPolicy> activePolicies = discountPolicyRepository.findAll().stream()
                .filter(DiscountPolicy::getIsActive)
                .filter(p -> excludeId == null || !p.getId().equals(excludeId))
                .collect(Collectors.toList());

        for (DiscountPolicy existing : activePolicies) {
            if (rangesOverlap(minQuantity, maxQuantity, existing.getMinQuantity(), existing.getMaxQuantity())) {
                throw new BadRequestException(
                        String.format("Conflict: An active discount policy already exists for overlapping quantity range [%d-%d]. " +
                                        "Existing policy ID: %d covers range [%d-%d] with %.0f%% discount.",
                                minQuantity, maxQuantity,
                                existing.getId(),
                                existing.getMinQuantity(), existing.getMaxQuantity(),
                                existing.getDiscountRate().multiply(new java.math.BigDecimal("100")))
                );
            }
        }
    }

    /**
     * Kiểm tra xem hai khoảng số lượng có trùng lặp không
     * @return true nếu hai khoảng có phần giao nhau
     */
    private boolean rangesOverlap(Integer min1, Integer max1, Integer min2, Integer max2) {
        return min1 <= max2 && min2 <= max1;
    }

    private DiscountPolicyResponse mapToResponse(DiscountPolicy policy) {
        return DiscountPolicyResponse.builder()
                .id(policy.getId())
                .minQuantity(policy.getMinQuantity())
                .maxQuantity(policy.getMaxQuantity())
                .discountRate(policy.getDiscountRate())
                .isActive(policy.getIsActive())
                .description(policy.getDescription())
                .build();
    }
}

