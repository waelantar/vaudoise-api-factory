package com.vaudoise.api_factory.application.usecase.contract;

import com.vaudoise.api_factory.domain.exception.ContractNotFoundException;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import com.vaudoise.api_factory.domain.repository.ContractRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateContractCostUseCase {

  private final ContractRepository contractRepository;

  public UpdateContractCostUseCase(ContractRepository contractRepository) {
    this.contractRepository = contractRepository;
  }

  public Contract execute(UUID contractId, Money newCost) {
    Contract contract =
        contractRepository
            .findById(contractId)
            .orElseThrow(
                () -> new ContractNotFoundException("Contract not found with id: " + contractId));

    contract.updateCost(newCost);
    return contractRepository.save(contract);
  }
}
