package com.vaudoise.api_factory.infrastructure.persistence.mapper;

import com.vaudoise.api_factory.domain.model.*;
import com.vaudoise.api_factory.infrastructure.persistence.entity.ClientEntity;
import com.vaudoise.api_factory.infrastructure.persistence.entity.CompanyEntity;
import com.vaudoise.api_factory.infrastructure.persistence.entity.PersonEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

  // --- Domain ->  ---

  public ClientEntity to(Client client) {
    if (client instanceof Person person) {
      return toPerson(person);
    } else if (client instanceof Company company) {
      return toCompany(company);
    }
    throw new IllegalArgumentException("Unknown client type: " + client.getClass());
  }

  private PersonEntity toPerson(Person person) {
    PersonEntity entity = new PersonEntity();
    entity.setId(person.getId());
    entity.setName(person.getName());
    entity.setEmail(person.getEmail().value());
    entity.setPhone(person.getPhone().value());
    entity.setBirthdate(person.getBirthDate());
    return entity;
  }

  private CompanyEntity toCompany(Company company) {
    CompanyEntity entity = new CompanyEntity();
    entity.setId(company.getId());
    entity.setName(company.getName());
    entity.setEmail(company.getEmail().value());
    entity.setPhone(company.getPhone().value());
    entity.setCompanyIdentifier(company.getCompanyIdentifier().value());
    return entity;
  }

  // ---  -> Domain ---

  public Client toDomain(ClientEntity entity) {
    if (entity instanceof PersonEntity personEntity) {
      return toPersonDomain(personEntity);
    } else if (entity instanceof CompanyEntity companyEntity) {
      return toCompanyDomain(companyEntity);
    }
    throw new IllegalArgumentException("Unknown  entity type: " + entity.getClass());
  }

  private Person toPersonDomain(PersonEntity entity) {
    Person person =
        new Person(
            entity.getName(),
            new Email(entity.getEmail()),
            new PhoneNumber(entity.getPhone()),
            entity.getBirthdate());
    person.setId(entity.getId());
    return person;
  }

  private Company toCompanyDomain(CompanyEntity entity) {
    Company company =
        new Company(
            entity.getName(),
            new Email(entity.getEmail()),
            new PhoneNumber(entity.getPhone()),
            new CompanyIdentifier(entity.getCompanyIdentifier()));
    company.setId(entity.getId());
    return company;
  }
}
