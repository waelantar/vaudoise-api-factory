package com.vaudoise.api_factory.infrastructure.persistence.repository;

import com.vaudoise.api_factory.infrastructure.persistence.entity.ClientEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaClientRepository extends JpaRepository<ClientEntity, UUID> {
  boolean existsById(UUID id);

  Page<ClientEntity> findAll(Pageable pageable);

  Optional<ClientEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  @Query(
      "SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CompanyEntity c WHERE c.companyIdentifier = :identifier")
  boolean existsByCompanyIdentifier(@Param("identifier") String identifier);
}
