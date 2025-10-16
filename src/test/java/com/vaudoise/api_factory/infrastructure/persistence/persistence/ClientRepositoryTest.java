package com.vaudoise.api_factory.infrastructure.persistence.persistence;

import static org.junit.jupiter.api.Assertions.*;

import com.vaudoise.api_factory.AbstractIntegrationTest;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.Email;
import com.vaudoise.api_factory.domain.model.Person;
import com.vaudoise.api_factory.domain.model.PhoneNumber;
import com.vaudoise.api_factory.infrastructure.persistence.entity.CompanyEntity;
import com.vaudoise.api_factory.infrastructure.persistence.entity.PersonEntity;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class ClientRepositoryTest extends AbstractIntegrationTest {

  private PersonEntity testPerson;
  private CompanyEntity testCompany;

  @BeforeEach
  void setUp() {
    testPerson = new PersonEntity();
    testPerson.setName("John Doe");
    testPerson.setEmail("john.doe@example.com");
    testPerson.setPhone("+41791234567");
    testPerson.setBirthdate(LocalDate.of(1980, 1, 1));
    entityManager.persistAndFlush(testPerson);

    testCompany = new CompanyEntity();
    testCompany.setName("Test Company");
    testCompany.setEmail("info@testcompany.com");
    testCompany.setPhone("+41441234567");
    testCompany.setCompanyIdentifier("CHE-123.456.789");
    entityManager.persistAndFlush(testCompany);
  }

  @Test
  void whenFindById_thenReturnClient() {
    UUID personId = testPerson.getId();
    System.out.println("Looking for Person ID: " + personId);
    Optional<Client> found = clientRepository.findById(personId);

    assertTrue(found.isPresent());
    assertEquals(testPerson.getName(), found.get().getName());
  }

  @Test
  void whenFindById_thenReturnEmptyOptional() {
    Optional<Client> found = clientRepository.findById(UUID.randomUUID());

    assertFalse(found.isPresent());
  }

  @Test
  void whenSave_thenReturnSavedClient() {

    Person newPerson =
        new Person(
            "Jane Smith",
            new Email("jane.smith@example.com"),
            new PhoneNumber("+41791234568"),
            LocalDate.of(1990, 5, 15));

    Client saved = clientRepository.save(newPerson);

    assertNotNull(saved.getId());
    assertEquals(newPerson.getName(), saved.getName());

    Optional<Client> found = clientRepository.findById(saved.getId());
    assertTrue(found.isPresent());
    assertEquals(newPerson.getName(), found.get().getName());
  }

  @Test
  void whenDeleteById_thenUpdateContractsAndDeleteClient() {
    UUID clientId = testPerson.getId();

    clientRepository.deleteById(clientId);

    assertFalse(jpaClientRepository.existsById(clientId));
  }

  @Test
  void whenFindAll_thenReturnAllClients() {
    Pageable pageable = PageRequest.of(0, 10);

    Page<Client> clients = clientRepository.findAll(pageable);

    assertEquals(2, clients.getTotalElements());
  }

  @Test
  void whenFindByEmail_thenReturnClient() {
    Optional<Client> found = clientRepository.findByEmail("john.doe@example.com");

    assertTrue(found.isPresent());
    assertEquals(testPerson.getName(), found.get().getName());
  }

  @Test
  void whenFindByEmail_thenReturnEmptyOptional() {
    Optional<Client> found = clientRepository.findByEmail("nonexistent@example.com");

    assertFalse(found.isPresent());
  }

  @Test
  void whenExistsByEmail_thenReturnTrue() {
    boolean exists = clientRepository.existsByEmail("john.doe@example.com");

    assertTrue(exists);
  }

  @Test
  void whenExistsByEmail_thenReturnFalse() {
    boolean exists = clientRepository.existsByEmail("nonexistent@example.com");

    assertFalse(exists);
  }

  @Test
  void whenExistsByCompanyIdentifier_thenReturnTrue() {
    boolean exists = clientRepository.existsByCompanyIdentifier("CHE-123.456.789");

    assertTrue(exists);
  }

  @Test
  void whenExistsByCompanyIdentifier_thenReturnFalse() {
    boolean exists = clientRepository.existsByCompanyIdentifier("CHE-999.999.999");

    assertFalse(exists);
  }
}
