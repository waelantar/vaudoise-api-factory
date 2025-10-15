package com.vaudoise.api_factory.infrastructure.web.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PhoneNumberValidator();
    }

    @Test
    void shouldBeValidForCorrectPhoneNumbers() {
        assertThat(validator.isValid("+41791234567", null)).isTrue();
        assertThat(validator.isValid("079 123 45 67", null)).isTrue();
        assertThat(validator.isValid("+41.79.123.45.67", null)).isTrue();
        assertThat(validator.isValid("+1-202-555-0136", null)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldBeValidForNullOrEmpty(String phone) {
        assertThat(validator.isValid(phone, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abc",                 // Non-numeric
            "123",                 // Too short
            "+0123",               // Starts with 0 after +
    })
    void shouldBeInvalidForIncorrectPhoneNumbers(String phone) {
        assertThat(validator.isValid(phone, null)).isFalse();
    }
}
