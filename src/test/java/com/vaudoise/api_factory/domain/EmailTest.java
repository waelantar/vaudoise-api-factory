package com.vaudoise.api_factory.domain;

import com.vaudoise.api_factory.domain.model.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    void shouldCreateEmailWhenValidInputIsProvided() {
        String validEmail = "  Test@Example.COM  ";

        Email email = new Email(validEmail);

        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnCorrectDomain() {
        Email email = new Email("user@domain.co.uk");

        String domain = email.getDomain();

        assertThat(domain).isEqualTo("domain.co.uk");
    }



    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalid-email", "test@.com", "@test.com", "test@test"})
    void shouldThrowExceptionForInvalidEmails(String invalidEmail) {
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    void shouldThrowExceptionForEmailExceedingMaxLength() {
        String longEmail = "a".repeat(250) + "@example.com"; // 256 chars

        assertThatThrownBy(() -> new Email(longEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot exceed 255 characters");
    }
}
