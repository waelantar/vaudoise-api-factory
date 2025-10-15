package com.vaudoise.api_factory.infrastructure.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class NumberValidator implements ConstraintValidator<ValidNumber, String> {

  private double maxValue;

  @Override
  public void initialize(ValidNumber constraintAnnotation) {
    this.maxValue = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(String number, ConstraintValidatorContext context) {
    if (number == null || number.trim().isEmpty()) {
      return true; // Let @NotBlank handle the emptiness check
    }
    try {
      BigDecimal decimalValue = new BigDecimal(number.trim());
      return decimalValue.compareTo(BigDecimal.ZERO) > 0
          && decimalValue.compareTo(BigDecimal.valueOf(maxValue)) <= 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
