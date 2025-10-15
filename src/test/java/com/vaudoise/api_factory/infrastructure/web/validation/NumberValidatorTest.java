package com.vaudoise.api_factory.infrastructure.web.validation;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

class NumberValidatorTest {

    @Mock
    private ConstraintValidatorContext context; // Still required for the method signature

    private NumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NumberValidator();
        // Initialize with a default annotation (max = Double.MAX_VALUE)
        validator.initialize(createAnnotationWithMax(Double.MAX_VALUE));
    }

    // Helper method to create an annotation instance for testing
    private ValidNumber createAnnotationWithMax(double maxValue) {
        return new ValidNumber() {
            @Override
            public String message() {
                return "test message";
            }

            @Override
            public Class<?>[] groups() {
                return new Class<?>[0];
            }

            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public double max() {
                return maxValue;
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return ValidNumber.class;
            }
        };
    }

    @Test
    void shouldBeValidForPositiveNumbersWithDefaultMax() {
        // No need to re-initialize, @BeforeEach already did it
        assertThat(validator.isValid("10", context)).isTrue();
        assertThat(validator.isValid("10.5", context)).isTrue();
        assertThat(validator.isValid("0.01", context)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldBeValidForNullOrEmpty(String number) {
        // No need to re-initialize, @BeforeEach already did it
        assertThat(validator.isValid(number, context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-1",      // Negative
            "0",       // Zero
            "abc",     // Non-numeric
            "12.34.56" // Malformed number
    })
    void shouldBeInvalidForNonPositiveNumbers(String number) {
        // No need to re-initialize, @BeforeEach already did it
        assertThat(validator.isValid(number, context)).isFalse();
    }

    @Test
    void shouldRespectCustomMaxValue() {
        // Arrange: Re-initialize the validator with a custom max value
        validator.initialize(createAnnotationWithMax(100.0));

        // Assert
        assertThat(validator.isValid("50", context)).isTrue();
        assertThat(validator.isValid("100", context)).isTrue();
        assertThat(validator.isValid("100.00", context)).isTrue();
        assertThat(validator.isValid("100.01", context)).isFalse(); // Over the max
    }
}
