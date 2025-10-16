package com.vaudoise.api_factory.infrastructure.web.validation;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyIdentifierValidatorTest {

  @Mock private ConstraintValidatorContext context;

  private CompanyIdentifierValidator validator;

  @BeforeEach
  void setUp() {
    validator = new CompanyIdentifierValidator();
  }

  @Test
  void shouldReturnTrueForValidSwissUid() {
    String validIdentifier = "CHE-123.456.789";

    boolean result = validator.isValid(validIdentifier, context);

    assertTrue(result);
  }

  @Test
  void shouldReturnTrueForValidSwissUidWithDifferentNumbers() {
    String validIdentifier = "CHE-999.888.777";

    boolean result = validator.isValid(validIdentifier, context);

    assertTrue(result);
  }

  @Test
  void shouldReturnTrueForValidSwissUidInLowerCase() {
    String validIdentifier = "che-123.456.789";

    boolean result = validator.isValid(validIdentifier, context);

    assertTrue(result);
  }

  @Test
  void shouldReturnTrueForValidSwissUidWithSpaces() {
    String validIdentifier = "  CHE-123.456.789  ";

    boolean result = validator.isValid(validIdentifier, context);

    assertTrue(result);
  }

  @Test
  void shouldReturnTrueForNullIdentifier() {
    String nullIdentifier = null;

    boolean result = validator.isValid(nullIdentifier, context);

    assertTrue(result);
  }

  @Test
  void shouldReturnTrueForEmptyIdentifier() {
    String emptyIdentifier = "";

    boolean result = validator.isValid(emptyIdentifier, context);

    assertTrue(result);
  }

  @Test
  void shouldReturnTrueForBlankIdentifier() {
    String blankIdentifier = "   ";

    boolean result = validator.isValid(blankIdentifier, context);

    assertTrue(result);
  }

  @Test
  void shouldReturnFalseForInvalidSwissUidWithWrongPrefix() {
    String invalidIdentifier = "CH-123.456.789";

    boolean result = validator.isValid(invalidIdentifier, context);

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseForInvalidSwissUidWithWrongFormat() {
    String invalidIdentifier = "CHE-123456789";

    boolean result = validator.isValid(invalidIdentifier, context);

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseForInvalidSwissUidWithWrongSeparator() {
    String invalidIdentifier = "CHE-123_456_789";

    boolean result = validator.isValid(invalidIdentifier, context);

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseForInvalidSwissUidWithTooManyDigits() {
    String invalidIdentifier = "CHE-1234.567.890";

    boolean result = validator.isValid(invalidIdentifier, context);

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseForInvalidSwissUidWithTooFewDigits() {
    String invalidIdentifier = "CHE-12.345.678";

    boolean result = validator.isValid(invalidIdentifier, context);

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseForInvalidSwissUidWithLettersInNumberPart() {
    String invalidIdentifier = "CHE-12A.45B.78C";

    boolean result = validator.isValid(invalidIdentifier, context);

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseForRandomString() {
    String invalidIdentifier = "random-string";

    boolean result = validator.isValid(invalidIdentifier, context);

    assertFalse(result);
  }
}
