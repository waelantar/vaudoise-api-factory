package com.vaudoise.api_factory.infrastructure.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNumber {
  String message() default "Invalid number format or value must be positive";

  // Optional attribute to set a maximum value
  double max() default Double.MAX_VALUE;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
