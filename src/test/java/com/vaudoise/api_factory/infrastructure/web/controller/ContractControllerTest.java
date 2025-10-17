package com.vaudoise.api_factory.infrastructure.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaudoise.api_factory.application.dto.request.CreateContractRequest;
import com.vaudoise.api_factory.application.dto.request.UpdateContractCostRequest;
import com.vaudoise.api_factory.application.usecase.contract.*;
import com.vaudoise.api_factory.domain.exception.ContractNotFoundException;
import com.vaudoise.api_factory.domain.model.Client;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import com.vaudoise.api_factory.domain.model.Person;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ContractController.class)
@DisplayName("Contract Controller Tests")
class ContractControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CreateContractUseCase createContractUseCase;

  @MockitoBean private GetActiveContractsUseCase getActiveContractsUseCase;

  @MockitoBean private UpdateContractCostUseCase updateContractCostUseCase;

  @MockitoBean private CalculateTotalCostUseCase calculateTotalCostUseCase;

  private UUID contractId;
  private UUID clientId;
  private Client client;
  private Contract contract;
  private CreateContractRequest createContractRequest;
  private UpdateContractCostRequest updateContractCostRequest;

  @BeforeEach
  void setUp() {
    contractId = UUID.randomUUID();
    clientId = UUID.randomUUID();

    client =
        new Person(
            "John Doe",
            new com.vaudoise.api_factory.domain.model.Email("john.doe@example.com"),
            new com.vaudoise.api_factory.domain.model.PhoneNumber("+41791234567"),
            LocalDate.of(1990, 1, 1));
    client.setId(clientId);

    Money costAmount = Money.chf(new BigDecimal("1000.00"));
    contract = new Contract(client, costAmount, LocalDate.now(), null);
    contract.setId(contractId);
    contract.setCreatedAt(Instant.now());

    createContractRequest = new CreateContractRequest("1000.00", "2023-01-01", "2024-01-01");
    updateContractCostRequest = new UpdateContractCostRequest("1500.00");
  }

  @Test
  @DisplayName("Create Contract - Should return 201 Created when request is valid")
  void createContract_ShouldReturn201_WhenValidRequest() throws Exception {
    given(
            createContractUseCase.execute(
                eq(clientId), any(Money.class), any(LocalDate.class), any(LocalDate.class)))
        .willReturn(contract);

    mockMvc
        .perform(
            post("/api/v1/contracts")
                .param("clientId", clientId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createContractRequest)))
        .andExpect(status().isCreated())
        .andExpect(
            header()
                .string(
                    "Location",
                    org.hamcrest.Matchers.endsWith("/api/v1/contracts/" + contractId.toString())))
        .andExpect(jsonPath("$.id").value(contractId.toString()))
        .andExpect(jsonPath("$.clientId").value(clientId.toString()))
        .andExpect(jsonPath("$.clientName").value("John Doe"))
        .andExpect(jsonPath("$.costAmount").value(1000.00))
        .andExpect(jsonPath("$.costCurrency").value("CHF"))
        .andExpect(jsonPath("$.active").value(true));
  }

  @Test
  @DisplayName("Create Contract - Should return 404 Not Found when client does not exist")
  void createContract_ShouldReturn404_WhenClientDoesNotExist() throws Exception {
    given(
            createContractUseCase.execute(
                eq(clientId), any(Money.class), any(LocalDate.class), any(LocalDate.class)))
        .willThrow(
            new com.vaudoise.api_factory.domain.exception.ClientNotFoundException(
                "Client not found"));

    mockMvc
        .perform(
            post("/api/v1/contracts")
                .param("clientId", clientId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createContractRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Client not found"));
  }

  @Test
  @DisplayName("Create Contract - Should return 400 Bad Request when input is invalid")
  void createContract_ShouldReturn400_WhenInputIsInvalid() throws Exception {
    CreateContractRequest invalidRequest =
        new CreateContractRequest("-1000.00", "invalid-date", "2023-01-01");

    mockMvc
        .perform(
            post("/api/v1/contracts")
                .param("clientId", clientId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Get Active Contracts - Should return 200 OK with paginated contracts")
  void getActiveContracts_ShouldReturn200_WhenClientExists() throws Exception {
    List<Contract> contracts = List.of(contract);
    Page<Contract> contractPage = new PageImpl<>(contracts, PageRequest.of(0, 10), 1);
    given(getActiveContractsUseCase.execute(eq(clientId), eq(null), any(Pageable.class)))
        .willReturn(contractPage);

    mockMvc
        .perform(
            get("/api/v1/contracts/active")
                .param("clientId", clientId.toString())
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(contractId.toString()))
        .andExpect(jsonPath("$.content[0].clientId").value(clientId.toString()))
        .andExpect(jsonPath("$.pageNumber").value(0))
        .andExpect(jsonPath("$.pageSize").value(10))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Get Active Contracts - Should return 404 Not Found when client does not exist")
  void getActiveContracts_ShouldReturn404_WhenClientDoesNotExist() throws Exception {
    given(getActiveContractsUseCase.execute(eq(clientId), eq(null), any(Pageable.class)))
        .willThrow(
            new com.vaudoise.api_factory.domain.exception.ClientNotFoundException(
                "Client not found"));

    mockMvc
        .perform(get("/api/v1/contracts/active").param("clientId", clientId.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Client not found"));
  }

  @Test
  @DisplayName("Update Contract Cost - Should return 200 OK when contract exists")
  void updateContractCost_ShouldReturn200_WhenContractExists() throws Exception {
    Money newCost = Money.chf(new BigDecimal("1500.00"));
    Contract updatedContract =
        new Contract(client, newCost, contract.getStartDate(), contract.getEndDate().orElse(null));
    updatedContract.setId(contractId);
    updatedContract.setCreatedAt(contract.getCreatedAt());

    given(updateContractCostUseCase.execute(eq(contractId), any(Money.class)))
        .willReturn(updatedContract);

    mockMvc
        .perform(
            put("/api/v1/contracts/{id}/cost", contractId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateContractCostRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(contractId.toString()))
        .andExpect(jsonPath("$.costAmount").value(1500.00))
        .andExpect(jsonPath("$.costCurrency").value("CHF"));
  }

  @Test
  @DisplayName("Update Contract Cost - Should return 404 Not Found when contract does not exist")
  void updateContractCost_ShouldReturn404_WhenContractDoesNotExist() throws Exception {
    given(updateContractCostUseCase.execute(eq(contractId), any(Money.class)))
        .willThrow(new ContractNotFoundException("Contract not found"));

    mockMvc
        .perform(
            put("/api/v1/contracts/{id}/cost", contractId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateContractCostRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Contract not found"));
  }

  @Test
  @DisplayName("Calculate Total Cost - Should return 200 OK with total cost")
  void calculateTotalCost_ShouldReturn200_WhenClientExists() throws Exception {
    Money totalCost = Money.chf(new BigDecimal("3000.00"));
    given(calculateTotalCostUseCase.execute(clientId)).willReturn(totalCost);

    mockMvc
        .perform(get("/api/v1/contracts/active/total-cost").param("clientId", clientId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(3000.00))
        .andExpect(jsonPath("$.currency").value("CHF"));
  }

  @Test
  @DisplayName("Calculate Total Cost - Should return 404 Not Found when client does not exist")
  void calculateTotalCost_ShouldReturn404_WhenClientDoesNotExist() throws Exception {
    given(calculateTotalCostUseCase.execute(clientId))
        .willThrow(
            new com.vaudoise.api_factory.domain.exception.ClientNotFoundException(
                "Client not found"));

    mockMvc
        .perform(get("/api/v1/contracts/active/total-cost").param("clientId", clientId.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Client not found"));
  }
}
