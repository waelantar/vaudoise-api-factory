package com.vaudoise.api_factory.infrastructure.persistence.repository;

import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.repository.ClientRepository;
import com.vaudoise.api_factory.infrastructure.persistence.entity.ClientEntity;
import com.vaudoise.api_factory.infrastructure.persistence.entity.ContractEntity;
import com.vaudoise.api_factory.infrastructure.persistence.mapper.ClientMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ClientRepositoryImpl implements ClientRepository {

  private final JpaClientRepository springRepo;
  private final JpaContractRepository contractRepo;
  private final ClientMapper mapper;

  public ClientRepositoryImpl(
      JpaClientRepository springRepo, JpaContractRepository contractRepo, ClientMapper mapper) {
    this.springRepo = springRepo;
    this.contractRepo = contractRepo;
    this.mapper = mapper;
  }

  @Override
  public Optional<Client> findById(UUID id) {
    return springRepo.findById(id).map(mapper::toDomain);
  }

  @Override
  @Transactional
  public Client save(Client client) {
    ClientEntity entity = mapper.to(client);
    ClientEntity savedEntity = springRepo.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    if (!springRepo.existsById(id)) {
      throw new ClientNotFoundException("Client not found with id: " + id);
    }

    List<ContractEntity> activeContracts = contractRepo.findAllActiveContractsForClient(id);

    activeContracts.forEach(
        contract -> {
          contract.setEndDate(LocalDate.now());
        });

    contractRepo.saveAll(activeContracts);

    springRepo.deleteById(id);
  }

  @Override
  public Page<Client> findAll(Pageable pageable) {
    Page<ClientEntity> entityPage = springRepo.findAll(pageable);
    return entityPage.map(mapper::toDomain);
  }

  @Override
  public Optional<Client> findByEmail(String email) {
    return springRepo.findByEmail(email).map(mapper::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return springRepo.existsByEmail(email);
  }

  @Override
  public boolean existsByCompanyIdentifier(String identifier) {
    return springRepo.existsByCompanyIdentifier(identifier);
  }
}
