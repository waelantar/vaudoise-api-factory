package com.vaudoise.api_factory.application.usecase.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.exception.DuplicateEmailException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.Email;
import com.vaudoise.api_factory.domain.model.PhoneNumber;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateClientUseCaseTest {

  @Mock private ClientRepository clientRepository;

  @Mock private Email oldEmail;

  @Mock private Email newEmail;

  @Mock private PhoneNumber phone;

  private UpdateClientUseCase updateClientUseCase;

  @BeforeEach
  void setUp() {
    updateClientUseCase = new UpdateClientUseCase(clientRepository);
  }

  @Test
  void shouldUpdateClientWhenClientExistsAndEmailIsNotDuplicate() {
    UUID clientId = UUID.randomUUID();
    Client existingClient = mock(Client.class);
    Client updatedClient = mock(Client.class);

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
    when(existingClient.getEmail()).thenReturn(oldEmail);
    when(updatedClient.getEmail()).thenReturn(newEmail);
    when(oldEmail.value()).thenReturn("old@example.com");
    when(newEmail.value()).thenReturn("new@example.com");
    when(clientRepository.existsByEmail("new@example.com")).thenReturn(false);
    when(clientRepository.save(existingClient)).thenReturn(existingClient);

    Client result = updateClientUseCase.execute(clientId, updatedClient);

    assertNotNull(result);
    verify(clientRepository).findById(clientId);
    verify(clientRepository).existsByEmail("new@example.com");
    verify(existingClient)
        .updateInfo(updatedClient.getName(), updatedClient.getEmail(), updatedClient.getPhone());
    verify(clientRepository).save(existingClient);
  }

  @Test
  void shouldUpdateClientWhenEmailIsUnchanged() {
    UUID clientId = UUID.randomUUID();
    Client existingClient = mock(Client.class);
    Client updatedClient = mock(Client.class);

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
    when(existingClient.getEmail()).thenReturn(newEmail);
    when(updatedClient.getEmail()).thenReturn(newEmail);
    when(newEmail.value()).thenReturn("same@example.com");
    when(clientRepository.save(existingClient)).thenReturn(existingClient);

    Client result = updateClientUseCase.execute(clientId, updatedClient);

    assertNotNull(result);
    verify(clientRepository).findById(clientId);
    verify(clientRepository, never()).existsByEmail(anyString());
    verify(existingClient)
        .updateInfo(updatedClient.getName(), updatedClient.getEmail(), updatedClient.getPhone());
    verify(clientRepository).save(existingClient);
  }

  @Test
  void shouldThrowClientNotFoundExceptionWhenClientDoesNotExist() {
    UUID clientId = UUID.randomUUID();
    Client updatedClient = mock(Client.class);
    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

    assertThrows(
        ClientNotFoundException.class, () -> updateClientUseCase.execute(clientId, updatedClient));
    verify(clientRepository).findById(clientId);
    verify(clientRepository, never()).save(any(Client.class));
  }

  @Test
  void shouldThrowDuplicateEmailExceptionWhenNewEmailAlreadyExists() {
    UUID clientId = UUID.randomUUID();
    Client existingClient = mock(Client.class);
    Client updatedClient = mock(Client.class);

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
    when(existingClient.getEmail()).thenReturn(oldEmail);
    when(updatedClient.getEmail()).thenReturn(newEmail);
    when(oldEmail.value()).thenReturn("old@example.com");
    when(newEmail.value()).thenReturn("new@example.com");
    when(clientRepository.existsByEmail("new@example.com")).thenReturn(true);

    assertThrows(
        DuplicateEmailException.class, () -> updateClientUseCase.execute(clientId, updatedClient));
    verify(clientRepository).findById(clientId);
    verify(clientRepository).existsByEmail("new@example.com");
    verify(existingClient, never()).updateInfo(any(), any(), any());
    verify(clientRepository, never()).save(any(Client.class));
  }
}
