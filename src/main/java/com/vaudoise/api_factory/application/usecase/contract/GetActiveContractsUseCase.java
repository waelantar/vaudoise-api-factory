package com.vaudoise.api_factory.application.usecase.contract;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import com.vaudoise.api_factory.domain.repository.ContractRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetActiveContractsUseCase {

  private final ContractRepository contractRepository;
  private final ClientRepository clientRepository;

  public GetActiveContractsUseCase(
      ContractRepository contractRepository, ClientRepository clientRepository) {
    this.contractRepository = contractRepository;
    this.clientRepository = clientRepository;
  }

  public Page<Contract> execute(UUID clientId, Instant updatedSince, Pageable pageable) {
    if (!clientRepository.findById(clientId).isPresent()) {
      throw new ClientNotFoundException("Client not found with id: " + clientId);
    }

    return contractRepository.findActiveContractsForClient(clientId, updatedSince, pageable);
  }
}
