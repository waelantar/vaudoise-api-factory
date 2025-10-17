package com.vaudoise.api_factory.infrastructure.persistence.mapper;

import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import com.vaudoise.api_factory.infrastructure.persistence.entity.ClientEntity;
import com.vaudoise.api_factory.infrastructure.persistence.entity.ContractEntity;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {
  private final ClientMapper clientMapper;

  public ContractMapper(ClientMapper clientMapper) {
    this.clientMapper = clientMapper;
  }

  public ContractEntity to(Contract contract) {
    if (contract == null) {
      return null;
    }
    ContractEntity entity = new ContractEntity();
    entity.setId(contract.getId());
    ClientEntity clientEntity = clientMapper.to(contract.getClient());
    entity.setClient(clientEntity);
    entity.setStartDate(contract.getStartDate());
    entity.setEndDate(contract.getEndDate().orElse(null));
    entity.setCostAmount(contract.getCostAmount().amount());
    entity.setCostCurrency(contract.getCostAmount().currency().getCurrencyCode());
    entity.setCreatedAt(contract.getCreatedAt());
    entity.setUpdatedAt(contract.getUpdateDate());
    return entity;
  }

  public Contract toDomain(ContractEntity entity) {
    if (entity == null) {
      return null;
    }

    ClientEntity clientEntity = Hibernate.unproxy(entity.getClient(), ClientEntity.class);
    Client client = clientMapper.toDomain(clientEntity);

    Money cost =
        new Money(entity.getCostAmount(), java.util.Currency.getInstance(entity.getCostCurrency()));
    Contract contract = new Contract(client, cost, entity.getStartDate(), entity.getEndDate());
    contract.setId(entity.getId());
    contract.setCreatedAt(entity.getCreatedAt());
    contract.setUpdateDate(entity.getUpdatedAt());
    return contract;
  }
}
