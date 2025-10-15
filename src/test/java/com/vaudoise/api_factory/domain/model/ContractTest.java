package com.vaudoise.api_factory.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContractTest {

  private Client testClient;
  private Money testCost;

  @BeforeEach
  void setUp() {
    testClient =
        new Person(
            "Test Person",
            new Email("test@example.com"),
            new PhoneNumber("+41791234567"),
            LocalDate.of(1990, 1, 1));
    testCost = Money.chf(100.00);
  }

  @Test
  void shouldCreateOpenEndedContractSuccessfully() {
    Contract contract = new Contract(testClient, testCost);

    assertThat(contract.getClient()).isEqualTo(testClient);
    assertThat(contract.getCostAmount()).isEqualTo(testCost);
    assertThat(contract.getStartDate()).isEqualTo(LocalDate.now());
    assertThat(contract.getEndDate()).isEmpty(); // Open-ended
    assertThat(contract.isActive()).isTrue();
  }

  @Test
  void shouldCreateFixedTermContractSuccessfully() {
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusYears(1);

    Contract contract = new Contract(testClient, testCost, startDate, endDate);

    assertThat(contract.getStartDate()).isEqualTo(startDate);
    assertThat(contract.getEndDate()).hasValue(endDate);
    assertThat(contract.isActive()).isTrue();
  }

  @Test
  void shouldThrowExceptionWhenEndDateIsBeforeStartDate() {
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.minusDays(1);

    assertThatThrownBy(() -> new Contract(testClient, testCost, startDate, endDate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("End date cannot be before start date");
  }

  @Test
  void shouldBeInactiveIfEndDateIsInThePast() {
    LocalDate startDate = LocalDate.now().minusYears(2);
    LocalDate endDate = LocalDate.now().minusYears(1);
    Contract contract = new Contract(testClient, testCost, startDate, endDate);

    assertThat(contract.isActive()).isFalse();
  }

  @Test
  void shouldTerminateContractAndSetEndDateToToday() {
    Contract contract = new Contract(testClient, testCost);
    Instant originalUpdateDate = contract.getUpdateDate();

    try {
      Thread.sleep(5); // Ensure timestamp difference
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    contract.terminate();

    assertThat(contract.isActive()).isFalse();
    assertThat(contract.getEndDate()).hasValue(LocalDate.now());
    assertThat(contract.getUpdateDate()).isAfter(originalUpdateDate);
  }

  @Test
  void shouldUpdateCostAndRefreshUpdateDate() {
    Contract contract = new Contract(testClient, testCost);
    Money newCost = Money.chf(150.00);
    Instant originalUpdateDate = contract.getUpdateDate();

    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    contract.updateCost(newCost);

    assertThat(contract.getCostAmount()).isEqualTo(newCost);
    assertThat(contract.getUpdateDate()).isAfter(originalUpdateDate);
  }

  @Test
  void shouldNotRefreshUpdateDateIfCostIsTheSame() {
    Contract contract = new Contract(testClient, testCost);
    Instant originalUpdateDate = contract.getUpdateDate();

    contract.updateCost(testCost); // Same cost

    assertThat(contract.getUpdateDate()).isEqualTo(originalUpdateDate);
  }

  @Test
  void shouldCalculateDurationCorrectly() {
    LocalDate startDate = LocalDate.of(2023, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 1, 1);
    Contract contract = new Contract(testClient, testCost, startDate, endDate);

    Period duration = contract.calculateDuration();

    assertThat(duration.getYears()).isEqualTo(1);
  }
}
