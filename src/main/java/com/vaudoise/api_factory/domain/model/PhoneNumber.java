package com.vaudoise.api_factory.domain.model;

import java.util.regex.Pattern;

/**
 * Value Object representing a phone number in E.164 format.
 * Immutable and self-validating.
 */
public record PhoneNumber(String value) {

    // E.164 format: +[country code][subscriber number]
    private static final Pattern E164_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{1,14}$"
    );

    public PhoneNumber {
        validate(value);
        value = normalize(value);
    }

    private void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid phone number format: Phone number cannot be empty");
        }

        String normalized = normalize(value);
        if (!E164_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Invalid phone number format. Expected E.164 format (e.g., +41791234567): " + value
            );
        }
    }

    private String normalize(String value) {
        // Remove spaces, dashes, parentheses
        String cleaned = value.replaceAll("[\\s\\-()]", "");

        // Ensure it starts with +
        if (!cleaned.startsWith("+")) {
            cleaned = "+" + cleaned;
        }

        return cleaned;
    }

    public String getCountryCode() {
        // Extract country code (1-3 digits after +)
        if (value.length() > 3) {
            return value.substring(1, Math.min(4, value.length()));
        }
        return value.substring(1);
    }

    public String format() {
        // Simple formatting: +XX XXX XXX XXX
        if (value.length() > 3) {
            return value.substring(0, 3) + " " +
                    value.substring(3).replaceAll("(.{3})", "$1 ").trim();
        }
        return value;
    }
}
