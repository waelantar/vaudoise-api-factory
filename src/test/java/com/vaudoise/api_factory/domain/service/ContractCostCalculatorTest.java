package com.vaudoise.api_factory.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.ClientType;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContractCostCalculatorTest {

  private ContractCostCalculator contractCostCalculator;

  @BeforeEach
  void setUp() {
    contractCostCalculator = new ContractCostCalculator();
  }

  @Test
  void shouldCalculateTotalCostForMultipleContractsWithSameCurrency() {
    Contract contract1 = createContractWithCost(BigDecimal.valueOf(1000), "CHF");
    Contract contract2 = createContractWithCost(BigDecimal.valueOf(2000), "CHF");
    Contract contract3 = createContractWithCost(BigDecimal.valueOf(500), "CHF");
    List<Contract> contracts = Arrays.asList(contract1, contract2, contract3);

    Money result = contractCostCalculator.calculateTotalCost(contracts);

    assertEquals(0, BigDecimal.valueOf(3500.00).compareTo(result.amount()));
    assertEquals(Currency.getInstance("CHF"), result.currency());
  }

  @Test
  void shouldCalculateTotalCostForSingleContract() {
    Contract contract = createContractWithCost(BigDecimal.valueOf(1500), "CHF");
    List<Contract> contracts = Collections.singletonList(contract);

    Money result = contractCostCalculator.calculateTotalCost(contracts);

    assertEquals(0, BigDecimal.valueOf(1500.00).compareTo(result.amount()));
    assertEquals(Currency.getInstance("CHF"), result.currency());
  }

  @Test
  void shouldThrowExceptionForContractsWithDifferentCurrencies() {
    Contract contract1 = createContractWithCost(BigDecimal.valueOf(1000), "CHF");
    Contract contract2 = createContractWithCost(BigDecimal.valueOf(2000), "USD");
    List<Contract> contracts = Arrays.asList(contract1, contract2);

    assertThrows(
        IllegalArgumentException.class, () -> contractCostCalculator.calculateTotalCost(contracts));
  }

  private Contract createContractWithCost(BigDecimal amount, String currencyCode) {
    Money cost = new Money(amount, Currency.getInstance(currencyCode));
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
