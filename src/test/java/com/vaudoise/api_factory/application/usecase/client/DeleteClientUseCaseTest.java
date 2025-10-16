package com.vaudoise.api_factory.application.usecase.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.ClientType;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteClientUseCaseTest {

  @Mock private ClientRepository clientRepository;

  private DeleteClientUseCase deleteClientUseCase;

  @BeforeEach
  void setUp() {
    deleteClientUseCase = new DeleteClientUseCase(clientRepository);
  }

  @Test
  void shouldDeleteClientWhenClientExists() {
    UUID clientId = UUID.randomUUID();
    Client client =
        new Client() {
          @Override
          public ClientType getType() {
            return null;
          }

          @Override
          public String getDisplayInfo() {
            return "";
          }
        };
    when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

    deleteClientUseCase.execute(clientId);

    verify(clientRepository).findById(clientId);
    verify(clientRepository).deleteById(clientId);
  }

  @Test
  void shouldThrowClientNotFoundExceptionWhenClientDoesNotExist() {
    UUID clientId = UUID.randomUUID();
    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

    assertThrows(ClientNotFoundException.class, () -> deleteClientUseCase.execute(clientId));
    verify(clientRepository).findById(clientId);
    verify(clientRepository, never()).deleteById(any(UUID.class));
  }
}
