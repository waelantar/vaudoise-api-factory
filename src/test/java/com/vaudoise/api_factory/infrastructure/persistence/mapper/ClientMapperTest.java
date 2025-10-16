package com.vaudoise.api_factory.infrastructure.persistence.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.vaudoise.api_factory.domain.model.*;
import com.vaudoise.api_factory.infrastructure.persistence.entity.ClientEntity;
import com.vaudoise.api_factory.infrastructure.persistence.entity.CompanyEntity;
import com.vaudoise.api_factory.infrastructure.persistence.entity.PersonEntity;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientMapperTest {

  private ClientMapper clientMapper;

  @BeforeEach
  void setUp() {
    clientMapper = new ClientMapper();
  }

  @Test
  void shouldMapPersonDomainToEntity() {
    UUID personId = UUID.randomUUID();
    Person person =
        new Person(
            "John Doe",
            new Email("john.doe@example.com"),
            new PhoneNumber("+41791234567"),
            LocalDate.of(1990, 5, 15));
    person.setId(personId);

    ClientEntity entity = clientMapper.to(person);

    assertThat(entity).isInstanceOf(PersonEntity.class);
    PersonEntity personEntity = (PersonEntity) entity;
    assertThat(personEntity.getId()).isEqualTo(personId);
    assertThat(personEntity.getName()).isEqualTo("John Doe");
    assertThat(personEntity.getEmail()).isEqualTo("john.doe@example.com");
    assertThat(personEntity.getPhone()).isEqualTo("+41791234567");
    assertThat(personEntity.getBirthdate()).isEqualTo(LocalDate.of(1990, 5, 15));
  }

  @Test
  void shouldMapCompanyDomainToEntity() {
    UUID companyId = UUID.randomUUID();
    Company company =
        new Company(
            "BizCorp",
            new Email("info@bizcorp.com"),
            new PhoneNumber("+41441234567"),
            new CompanyIdentifier("CHE-123.456.789"));
    company.setId(companyId);

    ClientEntity entity = clientMapper.to(company);

    assertThat(entity).isInstanceOf(CompanyEntity.class);
    CompanyEntity companyEntity = (CompanyEntity) entity;
    assertThat(companyEntity.getId()).isEqualTo(companyId);
    assertThat(companyEntity.getName()).isEqualTo("BizCorp");
    assertThat(companyEntity.getCompanyIdentifier()).isEqualTo("CHE-123.456.789");
  }

  @Test
  void shouldMapPersonEntityToDomain() {
    UUID personId = UUID.randomUUID();
    PersonEntity personEntity = new PersonEntity();
    personEntity.setId(personId);
    personEntity.setName("John Doe");
    personEntity.setEmail("john.doe@example.com");
    personEntity.setPhone("+41791234567");
    personEntity.setBirthdate(LocalDate.of(1990, 5, 15));

    Client client = clientMapper.toDomain(personEntity);

    assertThat(client).isInstanceOf(Person.class);
    Person person = (Person) client;
    assertThat(person.getId()).isEqualTo(personId);
    assertThat(person.getName()).isEqualTo("John Doe");
    assertThat(person.getEmail().value()).isEqualTo("john.doe@example.com");
    assertThat(person.getPhone().value()).isEqualTo("+41791234567");
    assertThat(person.getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 15));
  }

  @Test
  void shouldMapCompanyEntityToDomain() {
    UUID companyId = UUID.randomUUID();
    CompanyEntity companyEntity = new CompanyEntity();
    companyEntity.setId(companyId);
    companyEntity.setName("BizCorp");
    companyEntity.setEmail("info@bizcorp.com");
    companyEntity.setPhone("+41441234567");
    companyEntity.setCompanyIdentifier("CHE-123.456.789");

    Client client = clientMapper.toDomain(companyEntity);

    assertThat(client).isInstanceOf(Company.class);
    Company company = (Company) client;
    assertThat(company.getId()).isEqualTo(companyId);
    assertThat(company.getName()).isEqualTo("BizCorp");
    assertThat(company.getCompanyIdentifier().value()).isEqualTo("CHE-123.456.789");
  }

  @Test
  void shouldThrowExceptionForUnknownDomainType() {
    Client unknownClient =
        new Client() {
          @Override
          public ClientType getType() {
            return null;
          }

          @Override
          public String getDisplayInfo() {
            return null;
          }
        };

    assertThatThrownBy(() -> clientMapper.to(unknownClient))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown client type");
  }
}
