package com.example.ekyc_service.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotNullOrEmptyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)

public @interface NotNullOrEmpty {
    String message() default "value can't be null or empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
