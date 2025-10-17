package com.vaudoise.api_factory.domain.service;

import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Domain Service for complex calculations and business rules related to contract costs. This logic
 * doesn't belong to the Contract entity itself as it operates on a collection of contracts.
 */
@Service
public class ContractCostCalculator {

  /**
   * Calculates the total sum of costs for a given list of active contracts. It assumes all
   * contracts have the same currency (CHF in this case).
   *
   * @param contracts A list of contracts to sum the costs for.
   * @return A Money object representing the total cost. If the list is empty, returns Money.chf(0).
   * @throws IllegalArgumentException if contracts have different currencies.
   */
  public Money calculateTotalCost(List<Contract> contracts) {
    if (contracts == null || contracts.isEmpty()) {
      return Money.chf(BigDecimal.ZERO);
    }

    Currency currency = contracts.get(0).getCostAmount().currency();
    BigDecimal totalSum = BigDecimal.ZERO;

    for (Contract contract : contracts) {
      if (!contract.getCostAmount().currency().equals(currency)) {
        throw new IllegalArgumentException("Cannot sum contracts with different currencies.");
      }
      totalSum = totalSum.add(contract.getCostAmount().amount());
    }

    return new Money(totalSum, currency);
  }
}
