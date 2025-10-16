package com.vaudoise.api_factory.infrastructure.persistence.repository;

import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import com.vaudoise.api_factory.domain.repository.ContractRepository;
import com.vaudoise.api_factory.infrastructure.persistence.entity.ContractEntity;
import com.vaudoise.api_factory.infrastructure.persistence.mapper.ContractMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ContractRepositoryImpl implements ContractRepository {

  private final JpaContractRepository springRepo;
  private final ContractMapper mapper;

  public ContractRepositoryImpl(JpaContractRepository springRepo, ContractMapper mapper) {
    this.springRepo = springRepo;
    this.mapper = mapper;
  }

  @Override
  public Contract save(Contract contract) {
    ContractEntity entity = mapper.to(contract);
    ContractEntity savedEntity = springRepo.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<Contract> findById(UUID id) {
    return springRepo.findById(id).map(mapper::toDomain);
  }

  @Override
  public Page<Contract> findActiveContractsForClient(
      UUID clientId, Instant updatedSince, Pageable pageable) {
    Page<ContractEntity> entityPage;

    if (updatedSince != null) {
      entityPage =
          springRepo.findByClientIdAndEndDateIsNullOrEndDateAfterAndUpdatedAtAfter(
              clientId, LocalDate.now(), updatedSince, pageable);
    } else {
      entityPage =
          springRepo.findByClientIdAndEndDateIsNullOrEndDateAfter(
              clientId, LocalDate.now(), pageable);
    }

    return entityPage.map(mapper::toDomain);
  }

  @Override
  public List<Contract> findAllActiveContractsForClient(UUID clientId) {
    List<ContractEntity> entities = springRepo.findAllActiveContractsForClient(clientId);

    return entities.stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public Page<Contract> findAllContractsForClient(UUID clientId, Pageable pageable) {
    Page<ContractEntity> entityPage = springRepo.findByClientId(clientId, pageable);
    return entityPage.map(mapper::toDomain);
  }

  @Override
  public Money getActiveContractsCostSum(UUID clientId) {
    BigDecimal sum = springRepo.findActiveContractsCostSumByClientId(clientId, LocalDate.now());
    return new Money(sum == null ? BigDecimal.ZERO : sum, Currency.getInstance("CHF"));
  }
}
