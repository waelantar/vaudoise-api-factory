package com.vaudoise.api_factory.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/** Value Object representing monetary value. Immutable and self-validating. */
public record Money(BigDecimal amount, Currency currency) {

  private static final Currency CHF = Currency.getInstance("CHF");
  private static final int SCALE = 2;
  private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999.99");

  // Compact constructor for validation and normalization
  public Money {
    validate(amount);
    amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
    Objects.requireNonNull(currency, "Currency cannot be null");
  }

  // Static factory methods for convenience
  public static Money chf(BigDecimal amount) {
    return new Money(amount, CHF);
  }

  public static Money chf(double amount) {
    return new Money(BigDecimal.valueOf(amount), CHF);
  }

  // Private validation method
  private void validate(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive: " + amount);
    }
    if (amount.compareTo(MAX_AMOUNT) > 0) {
      throw new IllegalArgumentException("Amount exceeds maximum allowed value");
    }
  }

  // Business operations
  public Money add(Money other) {
    validateCurrency(other);
    return new Money(this.amount.add(other.amount), this.currency);
  }

  public Money subtract(Money other) {
    validateCurrency(other);
    BigDecimal result = this.amount.subtract(other.amount);
    if (result.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Result cannot be negative");
    }
    return new Money(result, this.currency);
  }

  public Money multiply(double multiplier) {
    if (multiplier <= 0) {
      throw new IllegalArgumentException("Multiplier must be positive");
    }
    return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
  }

  public boolean isPositive() {
    return amount.compareTo(BigDecimal.ZERO) > 0;
  }

  public boolean isGreaterThan(Money other) {
    validateCurrency(other);
    return this.amount.compareTo(other.amount) > 0;
  }

  private void validateCurrency(Money other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException(
          "Cannot perform operation on different currencies: "
              + this.currency
              + " vs "
              + other.currency);
    }
  }
}
