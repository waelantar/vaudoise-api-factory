package com.vaudoise.api_factory.infrastructure.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaudoise.api_factory.application.dto.request.CreateCompanyRequest;
import com.vaudoise.api_factory.application.dto.request.CreatePersonRequest;
import com.vaudoise.api_factory.application.dto.request.UpdateClientRequest;
import com.vaudoise.api_factory.application.usecase.client.*;
import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.exception.DuplicateEmailException;
import com.vaudoise.api_factory.domain.model.*;
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

@WebMvcTest(ClientController.class)
@DisplayName("Client Controller Tests")
class ClientControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CreateClientUseCase createClientUseCase;

  @MockitoBean private GetClientUseCase getClientUseCase;

  @MockitoBean private UpdateClientUseCase updateClientUseCase;

  @MockitoBean private DeleteClientUseCase deleteClientUseCase;

  private UUID personId;
  private UUID companyId;
  private Person person;
  private Company company;
  private CreatePersonRequest createPersonRequest;
  private CreateCompanyRequest createCompanyRequest;
  private UpdateClientRequest updateClientRequest;

  @BeforeEach
  void setUp() {
    personId = UUID.randomUUID();
    companyId = UUID.randomUUID();

    person =
        new Person(
            "John Doe",
            new Email("john.doe@example.com"),
            new PhoneNumber("+41791234567"),
            LocalDate.of(1990, 1, 1));
    person.setId(personId);
    person.setCreatedAt(Instant.now());

    company =
        new Company(
            "Vaudoise Assurances",
            new Email("info@vaudoise.ch"),
            new PhoneNumber("+41211234567"),
            new CompanyIdentifier("CHE-123.123.123"));
    company.setId(companyId);
    company.setCreatedAt(Instant.now());

    createPersonRequest =
        new CreatePersonRequest("John Doe", "john.doe@example.com", "+41791234567", "1990-01-01");
    createCompanyRequest =
        new CreateCompanyRequest(
            "Vaudoise Assurances", "info@vaudoise.ch", "+41211234567", "CHE-123.123.123");
    updateClientRequest =
        new UpdateClientRequest("Jane Doe", "jane.doe@example.com", "+41791234568");
  }

  @Test
  @DisplayName("Create Person - Should return 201 Created when request is valid")
  void createPerson_ShouldReturn201_WhenValidRequest() throws Exception {
    given(createClientUseCase.execute(any(Person.class))).willReturn(person);

    mockMvc
        .perform(
            post("/api/clients/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPersonRequest)))
        .andExpect(status().isCreated())
        .andExpect(
            header()
                .string(
                    "Location",
                    org.hamcrest.Matchers.endsWith("/api/clients/persons/" + personId.toString())))
        .andExpect(jsonPath("$.id").value(personId.toString()))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.phone").value("+41791234567"))
        .andExpect(jsonPath("$.type").value("PERSON"));
  }

  @Test
  @DisplayName("Create Person - Should return 409 Conflict when email already exists")
  void createPerson_ShouldReturn409_WhenEmailExists() throws Exception {
    given(createClientUseCase.execute(any(Person.class)))
        .willThrow(new DuplicateEmailException("Email already exists"));

    mockMvc
        .perform(
            post("/api/clients/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPersonRequest)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Email already exists"));
  }

  @Test
  @DisplayName("Create Person - Should return 400 Bad Request when input is invalid")
  void createPerson_ShouldReturn400_WhenInputIsInvalid() throws Exception {
    CreatePersonRequest invalidRequest =
        new CreatePersonRequest("", "invalid-email", "123", "future-date");

    mockMvc
        .perform(
            post("/api/clients/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Create Company - Should return 201 Created when request is valid")
  void createCompany_ShouldReturn201_WhenValidRequest() throws Exception {
    given(createClientUseCase.execute(any(Company.class))).willReturn(company);

    mockMvc
        .perform(
            post("/api/clients/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCompanyRequest)))
        .andExpect(status().isCreated())
        .andExpect(
            header()
                .string(
                    "Location",
                    org.hamcrest.Matchers.endsWith(
                        "/api/clients/companies/" + companyId.toString())))
        .andExpect(jsonPath("$.id").value(companyId.toString()))
        .andExpect(jsonPath("$.name").value("Vaudoise Assurances"))
        .andExpect(jsonPath("$.companyIdentifier").value("CHE-123.123.123"))
        .andExpect(jsonPath("$.type").value("COMPANY"));
  }

  @Test
  @DisplayName("Get Client by ID - Should return 200 OK for a Person")
  void getClient_ShouldReturn200_WhenPersonExists() throws Exception {
    given(getClientUseCase.execute(personId)).willReturn(person);

    mockMvc
        .perform(get("/api/clients/{id}", personId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(personId.toString()))
        .andExpect(jsonPath("$.type").value("PERSON"))
        .andExpect(jsonPath("$.birthDate").value("1990-01-01"));
  }

  @Test
  @DisplayName("Get Client by ID - Should return 404 Not Found when client does not exist")
  void getClient_ShouldReturn404_WhenClientDoesNotExist() throws Exception {
    given(getClientUseCase.execute(personId))
        .willThrow(new ClientNotFoundException("Client not found"));

    mockMvc
        .perform(get("/api/clients/{id}", personId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Client not found"));
  }

  @Test
  @DisplayName("Get All Clients - Should return paginated list of clients")
  void getAllClients_ShouldReturnPaginatedList() throws Exception {
    List<Client> clients = List.of(person, company);
    Page<Client> clientPage = new PageImpl<>(clients, PageRequest.of(0, 10), 2);
    given(getClientUseCase.execute(any(Pageable.class))).willReturn(clientPage);

    mockMvc
        .perform(get("/api/clients"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].type").value("PERSON"))
        .andExpect(jsonPath("$.content[1].type").value("COMPANY"))
        .andExpect(jsonPath("$.pageNumber").value(0))
        .andExpect(jsonPath("$.pageSize").value(10))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  @DisplayName("Update Client - Should return 200 OK when updating a Person")
  void updateClient_ShouldReturn200_WhenUpdatingPerson() throws Exception {
    Person updatedPerson =
        new Person(
            "Jane Doe",
            new Email("jane.doe@example.com"),
            new PhoneNumber("+41791234568"),
            person.getBirthDate());
    updatedPerson.setId(personId);
    updatedPerson.setCreatedAt(person.getCreatedAt());

    given(getClientUseCase.execute(personId)).willReturn(person);
    given(updateClientUseCase.execute(eq(personId), any(Person.class))).willReturn(updatedPerson);

    mockMvc
        .perform(
            put("/api/clients/{id}", personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateClientRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Jane Doe"))
        .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
        .andExpect(jsonPath("$.type").value("PERSON"));
  }

  @Test
  @DisplayName(
      "Update Client - Should return 404 Not Found when trying to update a non-existent client")
  void updateClient_ShouldReturn404_WhenClientDoesNotExist() throws Exception {
    given(getClientUseCase.execute(personId))
        .willThrow(new ClientNotFoundException("Client not found"));

    mockMvc
        .perform(
            put("/api/clients/{id}", personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateClientRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Delete Client - Should return 204 No Content on successful deletion")
  void deleteClient_ShouldReturn204_WhenClientExists() throws Exception {
    willDoNothing().given(deleteClientUseCase).execute(personId);

    mockMvc
        .perform(delete("/api/clients/{id}", personId))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));

    then(deleteClientUseCase).should().execute(personId);
  }
}
