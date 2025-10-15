package com.vaudoise.api_factory.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class MoneyTest {

  @Test
  void shouldCreateMoneyWithStaticFactory() {
    Money money = Money.chf(100.50);

    assertThat(money.amount()).isEqualTo(new BigDecimal("100.50"));
    assertThat(money.currency()).isEqualTo(Currency.getInstance("CHF"));
  }

  @Test
  void shouldScaleAmountOnCreation() {
    Money money = Money.chf(100.555);

    assertThat(money.amount()).isEqualTo(new BigDecimal("100.56"));
  }

  @Test
  void shouldThrowExceptionForNonPositiveAmount() {
    assertThatThrownBy(() -> Money.chf(0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Amount must be positive");

    assertThatThrownBy(() -> Money.chf(-10))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Amount must be positive");
  }

  @Test
  void shouldAddTwoMoneyObjects() {
    Money money1 = Money.chf(50.00);
    Money money2 = Money.chf(25.50);

    Money result = money1.add(money2);

    assertThat(result.amount()).isEqualTo(new BigDecimal("75.50"));
  }

  @Test
  void shouldSubtractTwoMoneyObjects() {
    Money money1 = Money.chf(100.00);
    Money money2 = Money.chf(40.00);

    Money result = money1.subtract(money2);

    assertThat(result.amount()).isEqualTo(new BigDecimal("60.00"));
  }

  @Test
  void shouldThrowExceptionWhenSubtractionResultsInNegative() {
    Money money1 = Money.chf(20.00);
    Money money2 = Money.chf(30.00);

    assertThatThrownBy(() -> money1.subtract(money2))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Result cannot be negative");
  }

  @Test
  void shouldMultiplyMoneyObject() {
    Money money = Money.chf(100.00);

    Money result = money.multiply(1.5);

    assertThat(result.amount()).isEqualTo(new BigDecimal("150.00"));
  }

  @Test
  void shouldThrowExceptionWhenAddingDifferentCurrencies() {
    Money chfMoney = Money.chf(100.00);
    Money eurMoney = new Money(BigDecimal.valueOf(100.00), Currency.getInstance("EUR"));

    assertThatThrownBy(() -> chfMoney.add(eurMoney))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cannot perform operation on different currencies");
  }

  @Test
  void shouldCorrectlyCompareMoneyObjects() {
    Money smallMoney = Money.chf(50.00);
    Money bigMoney = Money.chf(100.00);

    assertThat(bigMoney.isGreaterThan(smallMoney)).isTrue();
    assertThat(smallMoney.isGreaterThan(bigMoney)).isFalse();
  }
}
