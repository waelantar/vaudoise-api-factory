package com.vaudoise.api_factory.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaudoise.api_factory.infrastructure.web.validation.ValidIso8601Date;
import com.vaudoise.api_factory.infrastructure.web.validation.ValidNumber;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateContractRequest(
    @NotNull(message = "Cost amount is required")
        @Positive(message = "Cost amount must be positive")
        @ValidNumber(max = 999999999.99)
        String costAmount,
    @ValidIso8601Date(message = "Start date must be a valid ISO-8601 date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        String startDate,
    @ValidIso8601Date(message = "End date must be a valid ISO-8601 date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        String endDate) {}
