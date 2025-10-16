package com.vaudoise.api_factory.domain.repository;

import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContractRepository {
  Contract save(Contract contract);

  Optional<Contract> findById(UUID id);

  Page<Contract> findActiveContractsForClient(
      UUID clientId, Instant updatedSince, Pageable pageable);

  List<Contract> findAllActiveContractsForClient(UUID clientId);

  Page<Contract> findAllContractsForClient(UUID clientId, Pageable pageable);

  Money getActiveContractsCostSum(UUID clientId);
}
