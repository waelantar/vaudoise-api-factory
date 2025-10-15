package com.vaudoise.api_factory.infrastructure.web.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class EmailValidatorTest {

    private EmailValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EmailValidator();
    }

    @Test
    void shouldBeValidForCorrectEmails() {
        assertThat(validator.isValid("test@example.com", null)).isTrue();
        assertThat(validator.isValid("user.name+tag@domain.co.uk", null)).isTrue();
        assertThat(validator.isValid("  test@example.com  ", null)).isTrue(); // Handles trimming
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldBeValidForNullOrEmpty(String email) {
        assertThat(validator.isValid(email, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test@example",         // Missing TLD
            "test@.com",            // Missing domain name
            "@test.com",            // Missing local part
            "test.com",             // Missing @
            "test.test@",           // Ends with @
            "test space@example.com" // Contains space
    })
    void shouldBeInvalidForIncorrectEmails(String email) {
        assertThat(validator.isValid(email, null)).isFalse();
    }
}
