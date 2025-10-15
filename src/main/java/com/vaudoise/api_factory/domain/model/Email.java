package com.vaudoise.api_factory.domain.model;

import java.util.regex.Pattern;

/**
 * Value Object representing an email address.
 * Immutable and self-validating.
 */
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Email {
        validate(value);
        value = value.toLowerCase().trim();
    }

    private void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid email format: \" +Email cannot be empty");
        }

        String trimmed = value.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }

        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("Email cannot exceed 255 characters");
        }
    }

    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }
}
