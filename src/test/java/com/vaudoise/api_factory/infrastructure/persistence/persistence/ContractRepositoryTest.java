package com.vaudoise.api_factory.infrastructure.persistence.persistence;

import static org.junit.jupiter.api.Assertions.*;

import com.vaudoise.api_factory.AbstractIntegrationTest;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import com.vaudoise.api_factory.infrastructure.persistence.entity.ContractEntity;
import com.vaudoise.api_factory.infrastructure.persistence.entity.PersonEntity;
import com.vaudoise.api_factory.infrastructure.persistence.mapper.ContractMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class ContractRepositoryTest extends AbstractIntegrationTest {

  @Autowired private ContractMapper contractMapper;

  private static final ZoneId TEST_ZONE = ZoneOffset.UTC;

  private PersonEntity testPerson;
  private ContractEntity activeContract;
  private ContractEntity expiredContract;
  private ContractEntity futureContract;

  @BeforeEach
  void setUp() {
    LocalDate now = LocalDate.of(2025, 10, 17);
    Instant nowInstant = now.atStartOfDay(TEST_ZONE).toInstant();

    testPerson = new PersonEntity();
    testPerson.setName("John Doe");
    testPerson.setEmail("john.doe@example.com");
    testPerson.setPhone("+41791234567");
    testPerson.setBirthdate(LocalDate.of(1980, 1, 1));
    entityManager.persistAndFlush(testPerson);

    activeContract = new ContractEntity();
    activeContract.setClient(testPerson);
    activeContract.setStartDate(now.minusMonths(1));
    activeContract.setEndDate(now.plusMonths(1));
    activeContract.setCostAmount(new BigDecimal("100.00"));
    activeContract.setCostCurrency("CHF");
    activeContract.setUpdatedAt(now.minusDays(5).atStartOfDay(TEST_ZONE).toInstant());
    entityManager.persistAndFlush(activeContract);

    expiredContract = new ContractEntity();
    expiredContract.setClient(testPerson);
    expiredContract.setStartDate(now.minusMonths(2));
    expiredContract.setEndDate(now.minusDays(1));
    expiredContract.setCostAmount(new BigDecimal("200.00"));
    expiredContract.setCostCurrency("CHF");
    expiredContract.setUpdatedAt(now.minusDays(10).atStartOfDay(TEST_ZONE).toInstant());
    entityManager.persistAndFlush(expiredContract);

    futureContract = new ContractEntity();
    futureContract.setClient(testPerson);
    futureContract.setStartDate(now.plusMonths(1));
    futureContract.setEndDate(now.plusMonths(2));
    futureContract.setCostAmount(new BigDecimal("300.00"));
    futureContract.setCostCurrency("CHF");
    futureContract.setUpdatedAt(now.minusDays(1).atStartOfDay(TEST_ZONE).toInstant());
    entityManager.persistAndFlush(futureContract);
  }

  @Test
  void whenFindById_thenReturnContract() {
    Optional<Contract> found = contractRepository.findById(activeContract.getId());

    assertTrue(found.isPresent());
    assertEquals(activeContract.getCostAmount(), found.get().getCostAmount().amount());
  }

  @Test
  void whenFindById_thenReturnEmptyOptional() {
    Optional<Contract> found = contractRepository.findById(UUID.randomUUID());

    assertFalse(found.isPresent());
  }

  @Test
  void whenSave_thenReturnSavedContract() {
    Client domainClient = clientRepository.findById(testPerson.getId()).orElseThrow();
    Contract newContract =
        new Contract(
            domainClient,
            new Money(new BigDecimal("500.00"), Currency.getInstance("CHF")),
            LocalDate.of(2025, 10, 18),
            LocalDate.of(2026, 10, 18));

    Contract saved = contractRepository.save(newContract);

    assertNotNull(saved.getId());
    assertEquals(newContract.getCostAmount().amount(), saved.getCostAmount().amount());

    Optional<Contract> found = contractRepository.findById(saved.getId());
    assertTrue(found.isPresent());
    assertEquals(newContract.getCostAmount().amount(), found.get().getCostAmount().amount());
  }

  @Test
  void whenFindActiveContractsForClient_thenReturnActiveContracts() {
    Pageable pageable = PageRequest.of(0, 10);

    Page<Contract> contracts =
        contractRepository.findActiveContractsForClient(testPerson.getId(), null, pageable);

    assertEquals(2, contracts.getTotalElements());
  }

  @Test
  void whenFindAllActiveContractsForClient_thenReturnActiveContracts() {
    List<Contract> contracts =
        contractRepository.findAllActiveContractsForClient(testPerson.getId());

    assertEquals(2, contracts.size());
  }

  @Test
  void whenFindAllContractsForClient_thenReturnAllContracts() {
    Pageable pageable = PageRequest.of(0, 10);

    Page<Contract> contracts =
        contractRepository.findAllContractsForClient(testPerson.getId(), pageable);

    assertEquals(3, contracts.getTotalElements());
  }

  @Test
  void whenGetActiveContractsCostSum_thenReturnCorrectSum() {
    Money sum = contractRepository.getActiveContractsCostSum(testPerson.getId());

    assertEquals(new BigDecimal("400.00"), sum.amount());
  }
}
