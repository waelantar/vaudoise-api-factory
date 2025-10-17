package com.vaudoise.api_factory.application.usecase.contract;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.ClientType;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import com.vaudoise.api_factory.domain.repository.ContractRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateContractUseCaseTest {

  @Mock private ContractRepository contractRepository;

  @Mock private ClientRepository clientRepository;

  private CreateContractUseCase createContractUseCase;

  @BeforeEach
  void setUp() {
    createContractUseCase = new CreateContractUseCase(contractRepository, clientRepository);
  }

  @Test
  void shouldCreateContractWithDatesWhenClientExists() {
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
    Money costAmount = new Money(BigDecimal.valueOf(1000), Currency.getInstance("CHF"));
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = LocalDate.now().plusMonths(12);

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
    when(contractRepository.save(any(Contract.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Contract result = createContractUseCase.execute(clientId, costAmount, startDate, endDate);

    assertNotNull(result);
    assertEquals(client, result.getClient());
    assertEquals(costAmount, result.getCostAmount());

    assertEquals(startDate, result.getStartDate());
    assertEquals(Optional.of(endDate), result.getEndDate());

    verify(clientRepository).findById(clientId);
    verify(contractRepository).save(any(Contract.class));
  }

  @Test
  void shouldCreateContractWithoutDatesWhenClientExists() {
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
    Money costAmount = new Money(BigDecimal.valueOf(1000), Currency.getInstance("CHF"));

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
    when(contractRepository.save(any(Contract.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Contract result = createContractUseCase.execute(clientId, costAmount, null, null);

    assertNotNull(result);
    assertEquals(client, result.getClient());
    assertEquals(costAmount, result.getCostAmount());

    assertNotNull(result.getStartDate());
    assertEquals(Optional.empty(), result.getEndDate());

    verify(clientRepository).findById(clientId);
    verify(contractRepository).save(any(Contract.class));
  }

  @Test
  void shouldThrowClientNotFoundExceptionWhenClientDoesNotExist() {
    UUID clientId = UUID.randomUUID();
    Money costAmount = new Money(BigDecimal.valueOf(1000), Currency.getInstance("CHF"));
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = LocalDate.now().plusMonths(12);

    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

    assertThrows(
        ClientNotFoundException.class,
        () -> createContractUseCase.execute(clientId, costAmount, startDate, endDate));
    verify(clientRepository).findById(clientId);
    verify(contractRepository, never()).save(any(Contract.class));
  }
}
