package com.vaudoise.api_factory.application.dto.response;

import java.math.BigDecimal;

public record MoneyResponse(BigDecimal amount, String currency) {}
