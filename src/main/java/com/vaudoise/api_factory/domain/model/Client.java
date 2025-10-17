package com.vaudoise.api_factory.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class Client {
  private UUID id;
  private String name;
  private Email email;
  private PhoneNumber phone;
  private List<Contract> contracts;
  private Instant createdAt;
  private Instant updatedAt;

  protected Client() {
    this.contracts = new ArrayList<>();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  protected Client(String name, Email email, PhoneNumber phone) {
    this();
    validateName(name);
    this.name = name;
    this.email = Objects.requireNonNull(email, "Email cannot be null");
    this.phone = Objects.requireNonNull(phone, "Phone cannot be null");
  }

  public void updateInfo(String name, Email email, PhoneNumber phone) {
    validateName(name);
    this.name = name;
    this.email = Objects.requireNonNull(email, "Email cannot be null");
    this.phone = Objects.requireNonNull(phone, "Phone cannot be null");
    this.updatedAt = Instant.now();
  }

  public void addContract(Contract contract) {
    Objects.requireNonNull(contract, "Contract cannot be null");
    if (!this.contracts.contains(contract)) {
      this.contracts.add(contract);
      contract.setClient(this);
    }
  }

  public void terminateAllContracts() {
    this.contracts.forEach(Contract::terminate);
    this.updatedAt = Instant.now();
  }

  public boolean isActive() {
    return this.contracts.stream().anyMatch(Contract::isActive);
  }

  public List<Contract> getActiveContracts() {
    return this.contracts.stream().filter(Contract::isActive).toList();
  }

  private void validateName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }
    if (name.length() > 255) {
      throw new IllegalArgumentException("Name cannot exceed 255 characters");
    }
  }

  public abstract ClientType getType();

  public abstract String getDisplayInfo();

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public Email getEmail() {
    return email;
  }

  public PhoneNumber getPhone() {
    return phone;
  }

  public List<Contract> getContracts() {
    return new ArrayList<>(contracts);
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  protected void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Client client = (Client) o;
    return Objects.equals(id, client.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
