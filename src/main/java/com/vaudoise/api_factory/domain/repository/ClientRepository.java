package com.vaudoise.api_factory.domain.repository;

import com.vaudoise.api_factory.domain.model.Client;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientRepository {
  Optional<Client> findById(UUID id);

  Client save(Client client);

  void deleteById(UUID id);

  Optional<Client> findByIdWithContracts(UUID id);

  Page<Client> findAllWithContracts(Pageable pageable);

  Page<Client> findAll(Pageable pageable);

  Optional<Client> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByCompanyIdentifier(String identifier);
}
