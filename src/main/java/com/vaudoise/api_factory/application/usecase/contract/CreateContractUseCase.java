package com.vaudoise.api_factory.application.usecase.contract;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import com.vaudoise.api_factory.domain.repository.ContractRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateContractUseCase {

  private final ContractRepository contractRepository;
  private final ClientRepository clientRepository;

  public CreateContractUseCase(
      ContractRepository contractRepository, ClientRepository clientRepository) {
    this.contractRepository = contractRepository;
    this.clientRepository = clientRepository;
  }

  public Contract execute(UUID clientId, Money costAmount, LocalDate startDate, LocalDate endDate) {
    Client client =
        clientRepository
            .findById(clientId)
            .orElseThrow(
                () -> new ClientNotFoundException("Client not found with id: " + clientId));

    Contract contract;
    if (startDate != null || endDate != null) {
      contract = new Contract(client, costAmount, startDate, endDate);
    } else {
      contract = new Contract(client, costAmount);
    }

    return contractRepository.save(contract);
  }
}
