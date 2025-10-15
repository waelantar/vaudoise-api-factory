package com.vaudoise.api_factory.domain.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/** Person client entity. */
public class Person extends Client {

  private LocalDate birthDate;

  // For JPA/framework use
  protected Person() {
    super();
  }

  public Person(String name, Email email, PhoneNumber phone, LocalDate birthDate) {
    super(name, email, phone);
    validateBirthDate(birthDate);
    this.birthDate = birthDate;
  }

  // Business logic
  public int getAge() {
    return Period.between(this.birthDate, LocalDate.now()).getYears();
  }

  public boolean isMajor() {
    return getAge() >= 18;
  }

  // Validation
  private void validateBirthDate(LocalDate birthDate) {
    Objects.requireNonNull(birthDate, "Birth date cannot be null for Person");
    if (birthDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Birth date cannot be in the future");
    }
    if (getAge(birthDate) > 150) {
      throw new IllegalArgumentException("Birth date is not realistic (age > 150)");
    }
  }

  private int getAge(LocalDate birthDate) {
    return Period.between(birthDate, LocalDate.now()).getYears();
  }

  @Override
  public ClientType getType() {
    return ClientType.PERSON;
  }

  @Override
  public String getDisplayInfo() {
    return String.format("Person: %s (Age: %d)", getName(), getAge());
  }

  // Getters
  public LocalDate getBirthDate() {
    return birthDate;
  }

  // Note: birthDate is immutable after creation (business rule)
}
