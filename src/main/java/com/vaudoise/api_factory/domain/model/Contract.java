package com.vaudoise.api_factory.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;

/** Contract entity representing an insurance contract. */
public class Contract {

  private Long id;
  private Client client;
  private LocalDate startDate;
  private LocalDate endDate; // null means open-ended
  private Money costAmount;
  private Instant updateDate; // Auto-updated when cost changes
  private Instant createdAt;

  // For JPA/framework use
  protected Contract() {
    this.createdAt = Instant.now();
    this.updateDate = Instant.now();
  }

  // Constructor with default dates
  public Contract(Client client, Money costAmount) {
    this();
    this.client = Objects.requireNonNull(client, "Client cannot be null");
    this.costAmount = Objects.requireNonNull(costAmount, "Cost amount cannot be null");
    this.startDate = LocalDate.now(); // Default to today
    this.endDate = null; // Default to open-ended
  }

  // Constructor with explicit dates
  public Contract(Client client, Money costAmount, LocalDate startDate, LocalDate endDate) {
    this();
    this.client = Objects.requireNonNull(client, "Client cannot be null");
    this.costAmount = Objects.requireNonNull(costAmount, "Cost amount cannot be null");
    this.startDate = startDate != null ? startDate : LocalDate.now();
    validateEndDate(this.startDate, endDate);
    this.endDate = endDate;
  }

  // Business logic
  public boolean isActive() {
    LocalDate today = LocalDate.now();
    return endDate == null || endDate.isAfter(today);
  }

  public void terminate() {
    this.endDate = LocalDate.now();
    this.updateDate = Instant.now();
  }

  public void updateCost(Money newCost) {
    Objects.requireNonNull(newCost, "Cost amount cannot be null");
    if (!this.costAmount.equals(newCost)) {
      this.costAmount = newCost;
      refreshUpdateDate();
    }
  }

  public Period calculateDuration() {
    LocalDate end = endDate != null ? endDate : LocalDate.now();
    return Period.between(startDate, end);
  }

  public int getDurationInDays() {
    return (int)
        java.time.temporal.ChronoUnit.DAYS.between(
            startDate, endDate != null ? endDate : LocalDate.now());
  }

  // Auto-update logic
  private void refreshUpdateDate() {
    this.updateDate = Instant.now();
  }

  // Validation
  private void validateEndDate(LocalDate start, LocalDate end) {
    if (end != null && end.isBefore(start)) {
      throw new IllegalArgumentException(
          "End date cannot be before start date: start=" + start + ", end=" + end);
    }
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Client getClient() {
    return client;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public Optional<LocalDate> getEndDate() {
    return Optional.ofNullable(endDate);
  }

  public void setEndDate(LocalDate endDate) {
    validateEndDate(this.startDate, endDate);
    this.endDate = endDate;
    refreshUpdateDate();
  }

  public Money getCostAmount() {
    return costAmount;
  }

  public Instant getUpdateDate() {
    return updateDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdateDate(Instant updateDate) {
    this.updateDate = updateDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Contract contract = (Contract) o;
    return Objects.equals(id, contract.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return String.format(
        "Contract{id=%d, client=%s, cost=%s, active=%b}",
        id, client != null ? client.getName() : "null", costAmount, isActive());
  }
}
