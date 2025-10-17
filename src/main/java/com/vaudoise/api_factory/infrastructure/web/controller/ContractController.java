package com.vaudoise.api_factory.infrastructure.web.controller;

import com.vaudoise.api_factory.application.dto.request.CreateContractRequest;
import com.vaudoise.api_factory.application.dto.request.UpdateContractCostRequest;
import com.vaudoise.api_factory.application.dto.response.ContractResponse;
import com.vaudoise.api_factory.application.dto.response.ErrorResponse;
import com.vaudoise.api_factory.application.dto.response.MoneyResponse;
import com.vaudoise.api_factory.application.dto.response.PaginationResponse;
import com.vaudoise.api_factory.application.usecase.contract.*;
import com.vaudoise.api_factory.domain.model.Contract;
import com.vaudoise.api_factory.domain.model.Money;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/contracts")
@Tag(name = "Contracts", description = "Operations for managing insurance contracts")
public class ContractController {

  private final CreateContractUseCase createContractUseCase;
  private final GetActiveContractsUseCase getActiveContractsUseCase;
  private final UpdateContractCostUseCase updateContractCostUseCase;
  private final CalculateTotalCostUseCase calculateTotalCostUseCase;

  public ContractController(
      CreateContractUseCase createContractUseCase,
      GetActiveContractsUseCase getActiveContractsUseCase,
      UpdateContractCostUseCase updateContractCostUseCase,
      CalculateTotalCostUseCase calculateTotalCostUseCase) {
    this.createContractUseCase = createContractUseCase;
    this.getActiveContractsUseCase = getActiveContractsUseCase;
    this.updateContractCostUseCase = updateContractCostUseCase;
    this.calculateTotalCostUseCase = calculateTotalCostUseCase;
  }

  @PostMapping
  @Operation(summary = "Create a new contract")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Contract created successfully",
            content = @Content(schema = @Schema(implementation = ContractResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<ContractResponse> createContract(
      @Parameter(description = "Client ID") @RequestParam UUID clientId,
      @Valid @RequestBody CreateContractRequest request) {

    LocalDate startDate = request.startDate() != null ? LocalDate.parse(request.startDate()) : null;
    LocalDate endDate = request.endDate() != null ? LocalDate.parse(request.endDate()) : null;

    Money costAmount = Money.chf(new BigDecimal(request.costAmount()));

    Contract contract = createContractUseCase.execute(clientId, costAmount, startDate, endDate);
    ContractResponse response = mapToContractResponse(contract);

    URI location =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/v1/contracts/{id}")
            .buildAndExpand(contract.getId())
            .toUri();

    return ResponseEntity.created(location).body(response);
  }

  @GetMapping("/active")
  @Operation(summary = "Get active contracts for a client")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Active contracts retrieved successfully",
            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<PaginationResponse<ContractResponse>> getActiveContracts(
      @Parameter(description = "Client ID") @RequestParam UUID clientId,
      @Parameter(description = "Updated since timestamp") @RequestParam(required = false)
          Instant updatedSince,
      @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Contract> contractPage =
        getActiveContractsUseCase.execute(clientId, updatedSince, pageable);

    Page<ContractResponse> contractResponsePage = contractPage.map(this::mapToContractResponse);
    PaginationResponse<ContractResponse> response = PaginationResponse.of(contractResponsePage);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}/cost")
  @Operation(summary = "Update contract cost")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Contract cost updated successfully",
            content = @Content(schema = @Schema(implementation = ContractResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Contract not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<ContractResponse> updateContractCost(
      @Parameter(description = "Contract ID") @PathVariable UUID id,
      @Valid @RequestBody UpdateContractCostRequest request) {

    Money newCost = Money.chf(new BigDecimal(request.costAmount()));
    Contract contract = updateContractCostUseCase.execute(id, newCost);
    ContractResponse response = mapToContractResponse(contract);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/active/total-cost")
  @Operation(summary = "Calculate total cost of active contracts for a client")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Total cost calculated successfully",
            content = @Content(schema = @Schema(implementation = MoneyResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<MoneyResponse> calculateTotalCost(
      @Parameter(description = "Client ID") @RequestParam UUID clientId) {

    Money totalCost = calculateTotalCostUseCase.execute(clientId);
    MoneyResponse response =
        new MoneyResponse(totalCost.amount(), totalCost.currency().getCurrencyCode());

    return ResponseEntity.ok(response);
  }

  private ContractResponse mapToContractResponse(Contract contract) {
    return new ContractResponse(
        contract.getId(),
        contract.getClient().getId(),
        contract.getClient().getName(),
        contract.getStartDate(),
        contract.getEndDate().orElse(null),
        contract.getCostAmount().amount(),
        contract.getCostAmount().currency().getCurrencyCode(),
        contract.isActive(),
        contract.getCreatedAt());
  }
}
