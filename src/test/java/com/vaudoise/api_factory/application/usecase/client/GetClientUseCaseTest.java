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
class GetClientUseCaseTest {

  @Mock private ClientRepository clientRepository;

  private GetClientUseCase getClientUseCase;

  @BeforeEach
  void setUp() {
    getClientUseCase = new GetClientUseCase(clientRepository);
  }

  @Test
  void shouldReturnClientWhenIdExists() {
    // Given
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

    // When
    Client result = getClientUseCase.execute(clientId);

    // Then
    assertNotNull(result);
    assertEquals(client, result);
    verify(clientRepository).findById(clientId);
  }

  @Test
  void shouldThrowClientNotFoundExceptionWhenIdDoesNotExist() {
    // Given
    UUID clientId = UUID.randomUUID();
    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(ClientNotFoundException.class, () -> getClientUseCase.execute(clientId));
    verify(clientRepository).findById(clientId);
  }
}
