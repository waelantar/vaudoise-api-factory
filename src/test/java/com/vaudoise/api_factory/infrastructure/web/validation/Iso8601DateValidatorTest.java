package com.vaudoise.api_factory.infrastructure.web.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class Iso8601DateValidatorTest {

  private Iso8601DateValidator validator;

  @BeforeEach
  void setUp() {
    validator = new Iso8601DateValidator();
  }

  @Test
  void shouldBeValidForCorrectIso8601Dates() {
    assertThat(validator.isValid("2023-10-27", null)).isTrue();
    assertThat(validator.isValid("1999-12-31", null)).isTrue();
    assertThat(validator.isValid("  2023-01-01  ", null)).isTrue(); // Handles trimming
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldBeValidForNullOrEmpty(String date) {
    assertThat(validator.isValid(date, null)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "27-10-2023", // Wrong format
        "2023/10/27", // Wrong separator
        "not-a-date", // Not a date
        "2023-13-01", // Invalid month
        "2023-02-30" // Invalid day
      })
  void shouldBeInvalidForIncorrectDates(String date) {
    assertThat(validator.isValid(date, null)).isFalse();
  }
}
