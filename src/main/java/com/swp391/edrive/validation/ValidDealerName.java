package com.swp391.edrive.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DealerNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDealerName {

    String message() default "Dealer name does not exist in the database";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}