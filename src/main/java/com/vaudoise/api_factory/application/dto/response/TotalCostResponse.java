package com.vaudoise.api_factory.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TotalCostResponse(BigDecimal totalCost, String currency) {}
