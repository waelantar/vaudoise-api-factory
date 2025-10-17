package com.vaudoise.api_factory.infrastructure.web.controller;

import com.vaudoise.api_factory.application.dto.request.CreateCompanyRequest;
import com.vaudoise.api_factory.application.dto.request.CreatePersonRequest;
import com.vaudoise.api_factory.application.dto.request.UpdateClientRequest;
import com.vaudoise.api_factory.application.dto.response.*;
import com.vaudoise.api_factory.application.usecase.client.*;
import com.vaudoise.api_factory.domain.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("api/v1/clients")
@Tag(name = "Clients", description = "Operations for managing clients (Person and Company)")
public class ClientController {

  private final CreateClientUseCase createClientUseCase;
  private final GetClientUseCase getClientUseCase;
  private final UpdateClientUseCase updateClientUseCase;
  private final DeleteClientUseCase deleteClientUseCase;

  public ClientController(
      CreateClientUseCase createClientUseCase,
      GetClientUseCase getClientUseCase,
      UpdateClientUseCase updateClientUseCase,
      DeleteClientUseCase deleteClientUseCase) {
    this.createClientUseCase = createClientUseCase;
    this.getClientUseCase = getClientUseCase;
    this.updateClientUseCase = updateClientUseCase;
    this.deleteClientUseCase = deleteClientUseCase;
  }

  @PostMapping("/persons")
  @Operation(summary = "Create a new person client")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Person created successfully",
            content = @Content(schema = @Schema(implementation = PersonResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<PersonResponse> createPerson(
      @Valid @RequestBody CreatePersonRequest request) {
    Person person =
        new Person(
            request.name(),
            new Email(request.email()),
            new PhoneNumber(request.phone()),
            LocalDate.parse(request.birthDate()));

    Person createdPerson = (Person) createClientUseCase.execute(person);
    PersonResponse response = mapToPersonResponse(createdPerson);

    URI location =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/clients/persons/{id}")
            .buildAndExpand(createdPerson.getId())
            .toUri();

    return ResponseEntity.created(location).body(response);
  }

  @PostMapping("/companies")
  @Operation(summary = "Create a new company client")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Company created successfully",
            content = @Content(schema = @Schema(implementation = CompanyResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Email or company identifier already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<CompanyResponse> createCompany(
      @Valid @RequestBody CreateCompanyRequest request) {
    Company company =
        new Company(
            request.name(),
            new Email(request.email()),
            new PhoneNumber(request.phone()),
            new CompanyIdentifier(request.companyIdentifier()));

    Company createdCompany = (Company) createClientUseCase.execute(company);
    CompanyResponse response = mapToCompanyResponse(createdCompany);

    URI location =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/clients/companies/{id}")
            .buildAndExpand(createdCompany.getId())
            .toUri();

    return ResponseEntity.created(location).body(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a client by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Client found",
            content =
                @Content(schema = @Schema(oneOf = {PersonResponse.class, CompanyResponse.class}))),
        @ApiResponse(
            responseCode = "404",
            description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<ClientResponse> getClient(
      @Parameter(description = "Client ID") @PathVariable UUID id) {
    Client client = getClientUseCase.execute(id);
    ClientResponse response = mapToClientResponse(client);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(summary = "Get all clients with pagination")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Clients retrieved successfully",
            content = @Content(schema = @Schema(implementation = PaginationResponse.class)))
      })
  public ResponseEntity<PaginationResponse<ClientResponse>> getAllClients(
      @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Client> clientPage = getClientUseCase.execute(pageable);

    Page<ClientResponse> clientResponsePage = clientPage.map(this::mapToClientResponse);
    PaginationResponse<ClientResponse> response = PaginationResponse.of(clientResponsePage);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a client")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Client updated successfully",
            content =
                @Content(schema = @Schema(oneOf = {PersonResponse.class, CompanyResponse.class}))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<ClientResponse> updateClient(
      @Parameter(description = "Client ID") @PathVariable UUID id,
      @Valid @RequestBody UpdateClientRequest request) {

    Client existingClient = getClientUseCase.execute(id);

    Client updatedClient;
    if (existingClient.getType() == ClientType.PERSON) {
      Person person = (Person) existingClient;
      updatedClient =
          new Person(
              request.name(),
              new Email(request.email()),
              new PhoneNumber(request.phone()),
              person.getBirthDate());
    } else {
      Company company = (Company) existingClient;
      updatedClient =
          new Company(
              request.name(),
              new Email(request.email()),
              new PhoneNumber(request.phone()),
              company.getCompanyIdentifier());
    }

    Client savedClient = updateClientUseCase.execute(id, updatedClient);
    ClientResponse response = mapToClientResponse(savedClient);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a client")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteClient(
      @Parameter(description = "Client ID") @PathVariable UUID id) {
    deleteClientUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  private ClientResponse mapToClientResponse(Client client) {
    if (client instanceof Person person) {
      return mapToPersonResponse(person);
    } else if (client instanceof Company company) {
      return mapToCompanyResponse(company);
    }
    throw new IllegalArgumentException("Unknown client type: " + client.getClass());
  }

  private PersonResponse mapToPersonResponse(Person person) {
    List<ClientResponse.ContractSummary> contracts =
        person.getContracts().stream()
            .map(
                contract ->
                    new ClientResponse.ContractSummary(
                        contract.getId(), contract.getName(), contract.isActive()))
            .collect(Collectors.toList());

    return new PersonResponse(
        person.getId(),
        person.getName(),
        person.getEmail().value(),
        person.getPhone().value(),
        contracts,
        person.getCreatedAt(),
        person.getBirthDate(),
        person.getAge(),
        person.isMajor(),
        person.getType()
        );
  }

  private CompanyResponse mapToCompanyResponse(Company company) {
    List<ClientResponse.ContractSummary> contracts =
        company.getContracts().stream()
            .map(
                contract ->
                    new ClientResponse.ContractSummary(
                        contract.getId(), contract.getName(), contract.isActive()))
            .collect(Collectors.toList());

    return new CompanyResponse(
        company.getId(),
        company.getName(),
        company.getEmail().value(),
        company.getPhone().value(),
        contracts,
        company.getCreatedAt(),
        company.getCompanyIdentifier().value(),
        company.getType()
        );
  }
}
