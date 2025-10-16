package com.vaudoise.api_factory.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vaudoise.api_factory.domain.model.*;
import com.vaudoise.api_factory.infrastructure.persistence.entity.ContractEntity;
import com.vaudoise.api_factory.infrastructure.persistence.entity.PersonEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContractMapperTest {

  @Mock private ClientMapper clientMapper;

  private ContractMapper contractMapper;

  @BeforeEach
  void setUp() {
    contractMapper = new ContractMapper(clientMapper);
  }

  @Test
  void shouldMapContractDomainToJpaEntity() {
    UUID contractId = UUID.randomUUID();
    Person client =
        new Person(
            "Test Person", new Email("t@e.com"), new PhoneNumber("+41791234567"), LocalDate.now());
    PersonEntity clientJpa = new PersonEntity();
    Money cost = Money.chf(new BigDecimal("100.00"));
    Contract contract = new Contract(client, cost, LocalDate.now(), null);
    contract.setId(contractId);

    when(clientMapper.to(client)).thenReturn(clientJpa);

    ContractEntity entity = contractMapper.to(contract);

    assertThat(entity.getId()).isEqualTo(contractId);
    assertThat(entity.getClient()).isEqualTo(clientJpa);
    assertThat(entity.getCostAmount()).isEqualTo(new BigDecimal("100.00"));
    assertThat(entity.getCostCurrency()).isEqualTo("CHF");
    assertThat(entity.getEndDate()).isNull();

    verify(clientMapper).to(client);
  }

  @Test
  void shouldMapContractJpaEntityToDomain() {

    UUID contractId = UUID.randomUUID();
    PersonEntity clientJpa = new PersonEntity();
    Person client =
        new Person(
            "Test Person", new Email("t@e.com"), new PhoneNumber("+41791234567"), LocalDate.now());
    ContractEntity entity = new ContractEntity();
    entity.setId(contractId);
    entity.setClient(clientJpa);
    entity.setCostAmount(new BigDecimal("100.00"));
    entity.setCostCurrency("CHF");
    entity.setStartDate(LocalDate.now());
    entity.setEndDate(null);

    when(clientMapper.toDomain(clientJpa)).thenReturn(client);

    Contract contract = contractMapper.toDomain(entity);

    assertThat(contract.getId()).isEqualTo(contractId);
    assertThat(contract.getClient()).isEqualTo(client);
    assertThat(contract.getCostAmount().amount()).isEqualTo(new BigDecimal("100.00"));
    assertThat(contract.getCostAmount().currency()).isEqualTo(Currency.getInstance("CHF"));
    assertThat(contract.getEndDate()).isEmpty();

    verify(clientMapper).toDomain(clientJpa);
  }

  @Test
  void shouldReturnNullWhenMappingNullToJpa() {
    ContractEntity entity = contractMapper.to(null);

    assertThat(entity).isNull();
  }

  @Test
  void shouldReturnNullWhenMappingNullToDomain() {
    Contract contract = contractMapper.toDomain(null);

    assertThat(contract).isNull();
  }
}
