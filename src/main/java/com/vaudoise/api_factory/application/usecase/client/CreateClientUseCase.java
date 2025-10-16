package com.vaudoise.api_factory.application.usecase.client;

import com.vaudoise.api_factory.domain.exception.DuplicateEmailException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateClientUseCase {

  private final ClientRepository clientRepository;

  public CreateClientUseCase(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public Client execute(Client client) {
    if (clientRepository.existsByEmail(client.getEmail().value())) {
      throw new DuplicateEmailException("Email already exists: " + client.getEmail().value());
    }

    if (client.getType() == com.vaudoise.api_factory.domain.model.ClientType.COMPANY) {
      com.vaudoise.api_factory.domain.model.Company company =
          (com.vaudoise.api_factory.domain.model.Company) client;
      if (clientRepository.existsByCompanyIdentifier(company.getCompanyIdentifier().value())) {
        throw new DuplicateEmailException(
            "Company identifier already exists: " + company.getCompanyIdentifier().value());
      }
    }

    return clientRepository.save(client);
  }
}
