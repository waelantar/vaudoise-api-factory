package com.vaudoise.api_factory.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaudoise.api_factory.infrastructure.web.validation.ValidEmail;
import com.vaudoise.api_factory.infrastructure.web.validation.ValidIso8601Date;
import com.vaudoise.api_factory.infrastructure.web.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePersonRequest(
    @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,
    @NotBlank(message = "Email is required") @ValidEmail String email,
    @NotBlank(message = "Phone number is required") @ValidPhoneNumber String phone,
    @NotBlank(message = "Birth date is required")
        @ValidIso8601Date(message = "Birth date must be a valid ISO-8601 date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        String birthDate) {}
