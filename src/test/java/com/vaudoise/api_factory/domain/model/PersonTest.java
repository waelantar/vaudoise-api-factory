package com.vaudoise.api_factory.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class PersonTest {

  @Test
  void shouldCreatePersonSuccessfully() {
    Email email = new Email("john.doe@example.com");
    PhoneNumber phone = new PhoneNumber("+41791234567");
    LocalDate birthDate = LocalDate.of(1990, 5, 15);

    Person person = new Person("John Doe", email, phone, birthDate);

    assertThat(person.getName()).isEqualTo("John Doe");
    assertThat(person.getEmail()).isEqualTo(email);
    assertThat(person.getPhone()).isEqualTo(phone);
    assertThat(person.getBirthDate()).isEqualTo(birthDate);
    assertThat(person.getType()).isEqualTo(ClientType.PERSON);
    assertThat(person.getContracts()).isEmpty();
  }

  @Test
  void shouldCalculateAgeCorrectly() {
    LocalDate thirtyYearsAgo = LocalDate.now().minusYears(30);
    Person person =
        new Person(
            "Jane Doe", new Email("jane@ex.com"), new PhoneNumber("+41791111111"), thirtyYearsAgo);

    int age = person.getAge();

    assertThat(age).isEqualTo(30);
  }

  @Test
  void shouldDetermineIfPersonIsMajor() {
    LocalDate adultBirthDate = LocalDate.now().minusYears(25);
    Person adult =
        new Person("Adult", new Email("a@ex.com"), new PhoneNumber("+41791111111"), adultBirthDate);

    LocalDate minorBirthDate = LocalDate.now().minusYears(15);
    Person minor =
        new Person("Minor", new Email("m@ex.com"), new PhoneNumber("+41792222222"), minorBirthDate);

    assertThat(adult.isMajor()).isTrue();
    assertThat(minor.isMajor()).isFalse();
  }

  @Test
  void shouldThrowExceptionForFutureBirthDate() {
    LocalDate futureDate = LocalDate.now().plusDays(1);

    assertThatThrownBy(
            () ->
                new Person(
                    "Time Traveler",
                    new Email("tt@ex.com"),
                    new PhoneNumber("+41791234567"),
                    futureDate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Birth date cannot be in the future");
  }

  @Test
  void shouldReturnCorrectDisplayInfo() {
    LocalDate birthDate = LocalDate.now().minusYears(42);
    Person person =
        new Person(
            "Old Person", new Email("old@ex.com"), new PhoneNumber("+41791234567"), birthDate);

    String displayInfo = person.getDisplayInfo();

    assertThat(displayInfo).isEqualTo("Person: Old Person (Age: 42)");
  }

  @Test
  void shouldAddContractAndBecomeActive() {
    Person person =
        new Person(
            "Client",
            new Email("c@ex.com"),
            new PhoneNumber("+41791234567"),
            LocalDate.of(1980, 1, 1));
    Contract contract = new Contract(person, Money.chf(50.0));

    person.addContract(contract);

    assertThat(person.getContracts()).containsExactly(contract);
    assertThat(person.isActive()).isTrue();
  }

  @Test
  void shouldGetOnlyActiveContracts() {
    Person person =
        new Person(
            "Client",
            new Email("c@ex.com"),
            new PhoneNumber("+41791234567"),
            LocalDate.of(1980, 1, 1));
    Contract activeContract = new Contract(person, Money.chf(50.0));
    Contract terminatedContract = new Contract(person, Money.chf(75.0));
    terminatedContract.terminate();
    person.addContract(activeContract);
    person.addContract(terminatedContract);

    List<Contract> activeContracts = person.getActiveContracts();

    assertThat(activeContracts).containsExactly(activeContract);
  }

  @Test
  void shouldTerminateAllContracts() {
    // Given
    Person person =
        new Person(
            "Client",
            new Email("c@ex.com"),
            new PhoneNumber("+41791234567"),
            LocalDate.of(1980, 1, 1));
    person.addContract(new Contract(person, Money.chf(50.0)));
    person.addContract(new Contract(person, Money.chf(75.0)));
    Instant originalUpdatedAt = person.getUpdatedAt();

    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    person.terminateAllContracts();

    assertThat(person.isActive()).isFalse();
    assertThat(person.getActiveContracts()).isEmpty();
    assertThat(person.getUpdatedAt()).isAfter(originalUpdatedAt);
  }
}
