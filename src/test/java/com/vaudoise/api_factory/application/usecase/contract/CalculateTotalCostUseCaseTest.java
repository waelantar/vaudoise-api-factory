package com.vaudoise.api_factory.application.usecase.contract;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.ClientType;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import com.vaudoise.api_factory.domain.repository.ContractRepository;
import com.vaudoise.api_factory.domain.service.ContractCostCalculator;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateTotalCostUseCaseTest {

  @Mock private ContractRepository contractRepository;

  @Mock private ClientRepository clientRepository;

  @Mock private ContractCostCalculator contractCostCalculator;

  private CalculateTotalCostUseCase calculateTotalCostUseCase;

  @BeforeEach
  void setUp() {
    calculateTotalCostUseCase =
        new CalculateTotalCostUseCase(contractRepository, clientRepository, contractCostCalculator);
  }

  @Test
  void shouldCalculateTotalCostWhenClientExistsWithContracts() {
    UUID clientId = UUID.randomUUID();
    List<Contract> contracts =
        Arrays.asList(
            createContractWithCost(BigDecimal.valueOf(1000)),
            createContractWithCost(BigDecimal.valueOf(2000)));
    Money expectedTotal = new Money(BigDecimal.valueOf(3000), Currency.getInstance("CHF"));

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
    when(contractRepository.findAllActiveContractsForClient(clientId)).thenReturn(contracts);
    when(contractCostCalculator.calculateTotalCost(contracts)).thenReturn(expectedTotal);

    Money result = calculateTotalCostUseCase.execute(clientId);

    assertNotNull(result);
    assertEquals(expectedTotal, result);
    verify(clientRepository).findById(clientId);
    verify(contractRepository).findAllActiveContractsForClient(clientId);
    verify(contractCostCalculator).calculateTotalCost(contracts);
  }

  @Test
  void shouldReturnZeroWhenClientExistsWithNoContracts() {
    UUID clientId = UUID.randomUUID();
    List<Contract> contracts = Collections.emptyList();
    Money expectedTotal = new Money(BigDecimal.valueOf(0.01), Currency.getInstance("CHF"));

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
    when(contractRepository.findAllActiveContractsForClient(clientId)).thenReturn(contracts);
    when(contractCostCalculator.calculateTotalCost(contracts)).thenReturn(expectedTotal);

    Money result = calculateTotalCostUseCase.execute(clientId);

    assertNotNull(result);
    assertEquals(expectedTotal, result);
    verify(clientRepository).findById(clientId);
    verify(contractRepository).findAllActiveContractsForClient(clientId);
    verify(contractCostCalculator).calculateTotalCost(contracts);
  }

  @Test
  void shouldThrowClientNotFoundExceptionWhenClientDoesNotExist() {
    UUID clientId = UUID.randomUUID();

    when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

    assertThrows(ClientNotFoundException.class, () -> calculateTotalCostUseCase.execute(clientId));
    verify(clientRepository).findById(clientId);
    verify(contractRepository, never()).findAllActiveContractsForClient(any());
    verify(contractCostCalculator, never()).calculateTotalCost(any());
  }

  private Contract createContractWithCost(BigDecimal amount) {
    Money cost = new Money(amount, Currency.getInstance("CHF"));
    return new Contract(
        new Client() {
          @Override
          public ClientType getType() {
            return null;
          }

          @Override
          public String getDisplayInfo() {
            return "";
          }
        },
        cost);
  }
}
