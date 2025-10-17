package com.vaudoise.api_factory.application.usecase.contract;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.vaudoise.api_factory.domain.exception.ContractNotFoundException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.ClientType;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import com.vaudoise.api_factory.domain.repository.ContractRepository;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateContractCostUseCaseTest {

  @Mock private ContractRepository contractRepository;

  private UpdateContractCostUseCase updateContractCostUseCase;

  @BeforeEach
  void setUp() {
    updateContractCostUseCase = new UpdateContractCostUseCase(contractRepository);
  }

  @Test
  void shouldUpdateContractCostWhenContractExists() {
    UUID contractId = UUID.randomUUID();
    Money oldCost = new Money(BigDecimal.valueOf(1000), Currency.getInstance("CHF"));
    Money newCost = new Money(BigDecimal.valueOf(2000), Currency.getInstance("CHF"));

    Contract contract =
        new Contract(
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
            oldCost);

    when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
    when(contractRepository.save(any(Contract.class))).thenReturn(contract);

    Contract result = updateContractCostUseCase.execute(contractId, newCost);

    assertNotNull(result);
    verify(contractRepository).findById(contractId);
    verify(contractRepository).save(contract);

    assertEquals(newCost, result.getCostAmount());
  }

  @Test
  void shouldThrowContractNotFoundExceptionWhenContractDoesNotExist() {
    UUID contractId = UUID.randomUUID();
    Money newCost = new Money(BigDecimal.valueOf(2000), Currency.getInstance("CHF"));

    when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

    assertThrows(
        ContractNotFoundException.class,
        () -> updateContractCostUseCase.execute(contractId, newCost));
    verify(contractRepository).findById(contractId);
    verify(contractRepository, never()).save(any(Contract.class));
  }
}
