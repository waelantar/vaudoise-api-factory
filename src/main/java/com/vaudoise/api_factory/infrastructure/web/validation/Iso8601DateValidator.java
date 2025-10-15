package com.vaudoise.api_factory.infrastructure.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Iso8601DateValidator implements ConstraintValidator<ValidIso8601Date, String> {

  @Override
  public boolean isValid(String date, ConstraintValidatorContext context) {
    if (date == null || date.trim().isEmpty()) {
      return true; // Let @NotBlank handle the emptiness check
    }
    try {
      // LocalDate.parse() handles ISO-8601 by default
      LocalDate.parse(date.trim());
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }
}
