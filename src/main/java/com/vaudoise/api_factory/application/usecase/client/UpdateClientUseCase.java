package com.vaudoise.api_factory.application.usecase.client;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.exception.DuplicateEmailException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateClientUseCase {

  private final ClientRepository clientRepository;

  public UpdateClientUseCase(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public Client execute(UUID id, Client updatedClient) {
    Client existingClient =
        clientRepository
            .findById(id)
            .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));

    // Check if email is being changed and if the new email already exists
    if (!existingClient.getEmail().value().equals(updatedClient.getEmail().value())
        && clientRepository.existsByEmail(updatedClient.getEmail().value())) {
      throw new DuplicateEmailException(
          "Email already exists: " + updatedClient.getEmail().value());
    }

    // Update the client info
    existingClient.updateInfo(
        updatedClient.getName(), updatedClient.getEmail(), updatedClient.getPhone());

    return clientRepository.save(existingClient);
  }
}
