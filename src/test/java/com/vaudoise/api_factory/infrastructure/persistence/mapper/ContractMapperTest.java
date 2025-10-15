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
    // Given
    Person client =
        new Person(
            "Test Person", new Email("t@e.com"), new PhoneNumber("+41791234567"), LocalDate.now());
    PersonEntity clientJpa = new PersonEntity();
    Money cost = Money.chf(new BigDecimal("100.00"));
    Contract contract = new Contract(client, cost, LocalDate.now(), null);
    contract.setId(10L);

    // Mock the dependency
    when(clientMapper.to(client)).thenReturn(clientJpa);

    // When
    ContractEntity entity = contractMapper.to(contract);

    // Then
    assertThat(entity.getId()).isEqualTo(10L);
    assertThat(entity.getClient()).isEqualTo(clientJpa);
    assertThat(entity.getCostAmount()).isEqualTo(new BigDecimal("100.00"));
    assertThat(entity.getCostCurrency()).isEqualTo("CHF");
    assertThat(entity.getEndDate()).isNull();

    // Verify the dependency was called
    verify(clientMapper).to(client);
  }

  @Test
  void shouldMapContractJpaEntityToDomain() {
    // Given
    PersonEntity clientJpa = new PersonEntity();
    Person client =
        new Person(
            "Test Person", new Email("t@e.com"), new PhoneNumber("+41791234567"), LocalDate.now());
    ContractEntity entity = new ContractEntity();
    entity.setId(10L);
    entity.setClient(clientJpa);
    entity.setCostAmount(new BigDecimal("100.00"));
    entity.setCostCurrency("CHF");
    entity.setStartDate(LocalDate.now());
    entity.setEndDate(null);

    // Mock the dependency
    when(clientMapper.toDomain(clientJpa)).thenReturn(client);

    // When
    Contract contract = contractMapper.toDomain(entity);

    // Then
    assertThat(contract.getId()).isEqualTo(10L);
    assertThat(contract.getClient()).isEqualTo(client);
    assertThat(contract.getCostAmount().amount()).isEqualTo(new BigDecimal("100.00"));
    assertThat(contract.getCostAmount().currency()).isEqualTo(Currency.getInstance("CHF"));
    assertThat(contract.getEndDate()).isEmpty();

    // Verify the dependency was called
    verify(clientMapper).toDomain(clientJpa);
  }

  @Test
  void shouldReturnNullWhenMappingNullToJpa() {
    // When
    ContractEntity entity = contractMapper.to(null);

    // Then
    assertThat(entity).isNull();
  }

  @Test
  void shouldReturnNullWhenMappingNullToDomain() {
    // When
    Contract contract = contractMapper.toDomain(null);

    // Then
    assertThat(contract).isNull();
  }
}
