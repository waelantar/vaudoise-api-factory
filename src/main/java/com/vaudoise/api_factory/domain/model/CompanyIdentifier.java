package com.vaudoise.api_factory.domain.model;

import java.util.regex.Pattern;

/**
 * Value Object representing a Swiss company identifier (UID/IDE).
 * Format: CHE-XXX.XXX.XXX
 * Immutable and self-validating.
 */
public record CompanyIdentifier(String value) {

    // Swiss UID format: CHE-XXX.XXX.XXX
    private static final Pattern SWISS_UID_PATTERN = Pattern.compile(
            "^CHE-\\d{3}\\.\\d{3}\\.\\d{3}$"
    );

    public CompanyIdentifier {
        validate(value);
        value = value.toUpperCase().trim();
    }

    private void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Company identifier cannot be empty");
        }

        String normalized = value.toUpperCase().trim();
        if (!SWISS_UID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Invalid company identifier format. Expected CHE-XXX.XXX.XXX: " + value
            );
        }
    }

    public String format() {
        return value;
    }
}
