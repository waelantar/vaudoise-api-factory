package com.vaudoise.api_factory.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vaudoise.api_factory.domain.model.ClientType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CompanyResponse(
    UUID id,
    String name,
    String email,
    String phone,
    List<ClientResponse.ContractSummary> contracts,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") Instant createdAt,
    String companyIdentifier)
    implements ClientResponse {

  @Override
  public ClientType type() {
    return ClientType.COMPANY;
  }
}
