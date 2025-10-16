package com.vaudoise.api_factory;

import com.vaudoise.api_factory.domain.repository.ClientRepository;
import com.vaudoise.api_factory.domain.repository.ContractRepository;
import com.vaudoise.api_factory.infrastructure.persistence.mapper.ClientMapper;
import com.vaudoise.api_factory.infrastructure.persistence.mapper.ContractMapper;
import com.vaudoise.api_factory.infrastructure.persistence.repository.ClientRepositoryImpl;
import com.vaudoise.api_factory.infrastructure.persistence.repository.ContractRepositoryImpl;
import com.vaudoise.api_factory.infrastructure.persistence.repository.JpaClientRepository;
import com.vaudoise.api_factory.infrastructure.persistence.repository.JpaContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({
  ClientRepositoryImpl.class,
  ContractRepositoryImpl.class,
  ClientMapper.class,
  ContractMapper.class
})
public abstract class AbstractIntegrationTest {

  @Autowired protected TestEntityManager entityManager;

  @Autowired protected JpaClientRepository jpaClientRepository;

  @Autowired protected JpaContractRepository jpaContractRepository;

  @Autowired protected ClientRepository clientRepository;

  @Autowired protected ContractRepository contractRepository;

  @BeforeEach
  void setUp() {}
}
