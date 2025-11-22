package com.swp391.edrive.initializer;

import com.swp391.edrive.entity.DiscountPolicy;
import com.swp391.edrive.repository.DiscountPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(5)
public class DiscountPolicyInitializer implements CommandLineRunner {

    private final DiscountPolicyRepository discountPolicyRepository;

    @Override
    public void run(String... args) {
        if (discountPolicyRepository.count() == 0) {
            log.info("Initializing default discount policies...");

            // Chiết khấu 5% cho đơn hàng 1-5 xe
            DiscountPolicy policy1 = DiscountPolicy.builder()
                    .minQuantity(1)
                    .maxQuantity(5)
                    .discountRate(new BigDecimal("0.05"))
                    .isActive(true)
                    .description("Giảm 5% khi đặt từ 1-5 chiếc xe")
                    .build();

            // Chiết khấu 10% cho đơn hàng 6-10 xe
            DiscountPolicy policy2 = DiscountPolicy.builder()
                    .minQuantity(6)
                    .maxQuantity(10)
                    .discountRate(new BigDecimal("0.10"))
                    .isActive(true)
                    .description("Giảm 10% khi đặt từ 6-10 chiếc xe")
                    .build();

            // Chiết khấu 15% cho đơn hàng trên 10 xe
            DiscountPolicy policy3 = DiscountPolicy.builder()
                    .minQuantity(11)
                    .maxQuantity(Integer.MAX_VALUE)
                    .discountRate(new BigDecimal("0.15"))
                    .isActive(true)
                    .description("Giảm 15% khi đặt trên 11 chiếc xe")
                    .build();

            discountPolicyRepository.save(policy1);
            discountPolicyRepository.save(policy2);
            discountPolicyRepository.save(policy3);

            log.info("Default discount policies initialized successfully");
        } else {
            log.info("Discount policies already exist, skipping initialization");
        }
    }
}

