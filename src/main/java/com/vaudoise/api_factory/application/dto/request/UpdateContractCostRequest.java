package com.vaudoise.api_factory.application.dto.request;

import com.vaudoise.api_factory.infrastructure.web.validation.ValidNumber;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateContractCostRequest(
    @NotNull(message = "Cost amount is required")
        @Positive(message = "Cost amount must be positive")
        @ValidNumber(max = 999999999.99)
        String costAmount) {}
