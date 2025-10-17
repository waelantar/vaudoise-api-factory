package com.vaudoise.api_factory.application.dto.request;

import com.vaudoise.api_factory.infrastructure.web.validation.ValidEmail;
import com.vaudoise.api_factory.infrastructure.web.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
    @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,
    @NotBlank(message = "Email is required") @ValidEmail String email,
    @NotBlank(message = "Phone number is required") @ValidPhoneNumber String phone) {}
