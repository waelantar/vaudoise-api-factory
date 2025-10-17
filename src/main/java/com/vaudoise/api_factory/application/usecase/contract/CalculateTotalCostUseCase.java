package com.vaudoise.api_factory.application.usecase.contract;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import com.vaudoise.api_factory.domain.repository.ContractRepository;
import com.vaudoise.api_factory.domain.service.ContractCostCalculator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CalculateTotalCostUseCase {

  private final ContractRepository contractRepository;
  private final ClientRepository clientRepository;
  private final ContractCostCalculator contractCostCalculator;

  public CalculateTotalCostUseCase(
      ContractRepository contractRepository,
      ClientRepository clientRepository,
      ContractCostCalculator contractCostCalculator) {
    this.contractRepository = contractRepository;
    this.clientRepository = clientRepository;
    this.contractCostCalculator = contractCostCalculator;
  }

  public Money execute(UUID clientId) {
    if (!clientRepository.findById(clientId).isPresent()) {
      throw new ClientNotFoundException("Client not found with id: " + clientId);
    }

    List<Contract> activeContracts = contractRepository.findAllActiveContractsForClient(clientId);

    return contractCostCalculator.calculateTotalCost(activeContracts);
  }
}
