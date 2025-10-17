package com.vaudoise.api_factory.application.usecase.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.vaudoise.api_factory.domain.exception.DuplicateEmailException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.ClientType;
import com.vaudoise.api_factory.domain.model.Company;
import com.vaudoise.api_factory.domain.model.CompanyIdentifier;
import com.vaudoise.api_factory.domain.model.Email;
import com.vaudoise.api_factory.domain.model.Person;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateClientUseCaseTest {

  @Mock private ClientRepository clientRepository;

  @Mock private Email email;

  @Mock private CompanyIdentifier companyIdentifier;

  private CreateClientUseCase createClientUseCase;

  @BeforeEach
  void setUp() {
    createClientUseCase = new CreateClientUseCase(clientRepository);
  }

  @Test
  void shouldCreatePersonClientWhenEmailDoesNotExist() {
    // Given
    Person client = mock(Person.class);
    when(client.getEmail()).thenReturn(email);
    when(email.value()).thenReturn("test@example.com");
    when(clientRepository.existsByEmail(anyString())).thenReturn(false);
    when(clientRepository.save(any(Client.class))).thenReturn(client);

    // When
    Client result = createClientUseCase.execute(client);

    // Then
    assertNotNull(result);
    verify(clientRepository).existsByEmail("test@example.com");
    verify(clientRepository).save(client);
  }

  @Test
  void shouldCreateCompanyClientWhenEmailAndCompanyIdentifierDoNotExist() {
    // Given
    Company company = mock(Company.class);
    when(company.getEmail()).thenReturn(email);
    when(email.value()).thenReturn("company@example.com");
    when(company.getCompanyIdentifier()).thenReturn(companyIdentifier);
    when(companyIdentifier.value()).thenReturn("COMP123");
    when(company.getType()).thenReturn(ClientType.COMPANY);
    when(clientRepository.existsByEmail(anyString())).thenReturn(false);
    when(clientRepository.existsByCompanyIdentifier(anyString())).thenReturn(false);
    when(clientRepository.save(any(Client.class))).thenReturn(company);

    // When
    Client result = createClientUseCase.execute(company);

    // Then
    assertNotNull(result);
    verify(clientRepository).existsByEmail("company@example.com");
    verify(clientRepository).existsByCompanyIdentifier("COMP123");
    verify(clientRepository).save(company);
  }

  @Test
  void shouldThrowDuplicateEmailExceptionWhenEmailAlreadyExists() {
    // Given
    Client client = mock(Client.class);
    when(client.getEmail()).thenReturn(email);
    when(email.value()).thenReturn("existing@example.com");
    when(clientRepository.existsByEmail(anyString())).thenReturn(true);

    // When & Then
    assertThrows(DuplicateEmailException.class, () -> createClientUseCase.execute(client));
    verify(clientRepository).existsByEmail("existing@example.com");
    verify(clientRepository, never()).save(any(Client.class));
  }

  @Test
  void shouldThrowDuplicateEmailExceptionWhenCompanyIdentifierAlreadyExists() {
    // Given
    Company company = mock(Company.class);
    when(company.getEmail()).thenReturn(email);
    when(email.value()).thenReturn("company@example.com");
    when(company.getCompanyIdentifier()).thenReturn(companyIdentifier);
    when(companyIdentifier.value()).thenReturn("COMP123");
    when(company.getType()).thenReturn(ClientType.COMPANY);
    when(clientRepository.existsByEmail(anyString())).thenReturn(false);
    when(clientRepository.existsByCompanyIdentifier(anyString())).thenReturn(true);

    // When & Then
    assertThrows(DuplicateEmailException.class, () -> createClientUseCase.execute(company));
    verify(clientRepository).existsByEmail("company@example.com");
    verify(clientRepository).existsByCompanyIdentifier("COMP123");
    verify(clientRepository, never()).save(any(Client.class));
  }
}
