package com.vaudoise.api_factory.infrastructure.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9.\\-\\s]{7,}$");

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        return phone == null || phone.trim().isEmpty() || PHONE_PATTERN.matcher(phone).matches();
    }
}
