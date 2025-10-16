package com.vaudoise.api_factory.infrastructure.persistence.repository;

import com.vaudoise.api_factory.infrastructure.persistence.entity.ContractEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaContractRepository extends JpaRepository<ContractEntity, UUID> {
  Page<ContractEntity> findByClientIdAndEndDateIsNullOrEndDateGreaterThanAndUpdatedAtAfter(
      UUID clientId, LocalDate currentDate, Instant updatedSince, Pageable pageable);

  Page<ContractEntity> findByClientIdAndEndDateIsNullOrEndDateGreaterThan(
      UUID clientId, LocalDate currentDate, Pageable pageable);

  Page<ContractEntity> findByClientId(UUID clientId, Pageable pageable);

  @Query(
      "SELECT c FROM ContractEntity c WHERE c.client.id = :clientId AND (c.endDate IS NULL OR c.endDate > :currentDate)")
  List<ContractEntity> findAllActiveContractsForClient(
      @Param("clientId") UUID clientId, @Param("currentDate") LocalDate currentDate);

  @Query(
      "SELECT SUM(c.costAmount) FROM ContractEntity c WHERE c.client.id = :clientId AND (c.endDate IS NULL OR c.endDate > :currentDate)")
  BigDecimal findActiveContractsCostSumByClientId(
      @Param("clientId") UUID clientId, @Param("currentDate") LocalDate currentDate);
}
