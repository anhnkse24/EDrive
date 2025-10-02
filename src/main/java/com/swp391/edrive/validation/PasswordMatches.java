package com.swp391.edrive.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ ElementType.TYPE }) // áp dụng cho class, không phải field
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {

    String message() default "Password and Confirm Password do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
