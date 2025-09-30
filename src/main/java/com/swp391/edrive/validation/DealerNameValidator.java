package com.swp391.edrive.validation;

import com.swp391.edrive.repository.DealerRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class DealerNameValidator implements ConstraintValidator<ValidDealerName, String> {

    @Autowired
    private DealerRepository dealerRepository;

    @Override
    public boolean isValid(String dealerName, ConstraintValidatorContext context) {
        if (dealerName == null || dealerName.trim().isEmpty()) {
            return false; // vì bạn có @NotBlank rồi, dòng này chỉ để chắc chắn
        }
        return dealerRepository.existsByDealerName(dealerName);
    }
}