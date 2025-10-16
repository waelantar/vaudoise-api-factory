package com.vaudoise.api_factory.application.usecase.client;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeleteClientUseCase {

  private final ClientRepository clientRepository;

  public DeleteClientUseCase(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public void execute(UUID id) {
    if (!clientRepository.findById(id).isPresent()) {
      throw new ClientNotFoundException("Client not found with id: " + id);
    }
    clientRepository.deleteById(id);
  }
}
