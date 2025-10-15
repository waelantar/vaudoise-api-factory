package com.vaudoise.api_factory.infrastructure.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = Iso8601DateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIso8601Date {
    String message() default "Invalid date format. Expected ISO-8601 format (e.g., YYYY-MM-DD)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
