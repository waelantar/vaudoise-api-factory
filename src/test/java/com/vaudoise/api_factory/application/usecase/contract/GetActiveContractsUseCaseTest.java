package com.vaudoise.api_factory.application.usecase.contract;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.ClientType;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import com.vaudoise.api_factory.domain.repository.ContractRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GetActiveContractsUseCaseTest {

  @Mock private ContractRepository contractRepository;

  @Mock private ClientRepository clientRepository;

  @Mock private Pageable pageable;

  private GetActiveContractsUseCase getActiveContractsUseCase;

  @BeforeEach
  void setUp() {
    getActiveContractsUseCase = new GetActiveContractsUseCase(contractRepository, clientRepository);
  }

  @Test
  void shouldReturnActiveContractsWhenClientExists() {
    UUID clientId = UUID.randomUUID();
    Instant updatedSince = Instant.now().minusSeconds(3600);
    List<Contract> contracts = Arrays.asList(new Contract(), new Contract());
    Page<Contract> expectedPage = new PageImpl<>(contracts);

    when(clientRepository.findById(clientId))
        .thenReturn(
            Optional.of(
                new Client() {
                  @Override
                  public ClientType getType() {
                    return null;
                  }

                  @Override
                  public String getDisplayInfo() {
                    return "";
                  }
                }));
    when(contractRepository.findActiveContractsForClient(clientId, updatedSince, pageable))
        .thenReturn(expectedPage);

    Page<Contract> result = getActiveContractsUseCase.execute(clientId, updatedSince, pageable);

    assertNotNull(result);
    assertEquals(expectedPage, result);
    verify(clientRepository).findById(clientId);
    verify(contractRepository).findActiveContractsForClient(clientId, updatedSince, pageable);
  }

  @Test
  void shouldThrowClientNotFoundExceptionWhenClientDoesNotExist() {
    UUID clientId = UUID.randomUUID();
    Instant updatedSince = Instant.now().minusSeconds(3600);

    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

    assertThrows(
        ClientNotFoundException.class,
        () -> getActiveContractsUseCase.execute(clientId, updatedSince, pageable));
    verify(clientRepository).findById(clientId);
    verify(contractRepository, never()).findActiveContractsForClient(any(), any(), any());
  }
}
