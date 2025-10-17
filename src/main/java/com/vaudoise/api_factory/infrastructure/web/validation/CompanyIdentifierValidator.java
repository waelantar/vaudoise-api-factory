package com.vaudoise.api_factory.infrastructure.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CompanyIdentifierValidator
    implements ConstraintValidator<ValidCompanyIdentifier, String> {
  private static final Pattern SWISS_UID_PATTERN =
      Pattern.compile("^CHE-\\d{3}\\.\\d{3}\\.\\d{3}$");

  @Override
  public boolean isValid(String identifier, ConstraintValidatorContext context) {
    if (identifier == null || identifier.trim().isEmpty()) {
      return true;
    }
    return SWISS_UID_PATTERN.matcher(identifier.trim().toUpperCase()).matches();
  }
}
