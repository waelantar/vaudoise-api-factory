package com.vaudoise.api_factory.application.usecase.client;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetClientUseCase {
  private final ClientRepository clientRepository;

  public GetClientUseCase(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public Page<Client> execute(Pageable pageable) {
    return clientRepository.findAll(pageable);
  }

  public Client execute(UUID id) {
    return clientRepository
        .findById(id)
        .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));
  }

  public Client executeWithContracts(UUID id) {
    System.out.println("Fetching client with ID: {}" + id);
    return clientRepository
        .findByIdWithContracts(id)
        .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));
  }

  public Page<Client> executeWithContracts(Pageable pageable) {
    return clientRepository.findAllWithContracts(pageable);
  }
}
