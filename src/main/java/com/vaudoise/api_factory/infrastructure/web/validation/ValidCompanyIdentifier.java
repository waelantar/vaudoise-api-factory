package com.vaudoise.api_factory.infrastructure.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CompanyIdentifierValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCompanyIdentifier {
  String message() default "Invalid company identifier format. Expected CHE-XXX.XXX.XXX";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
