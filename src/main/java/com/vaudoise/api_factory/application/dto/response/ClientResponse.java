package com.vaudoise.api_factory.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = PersonResponse.class, name = "PERSON"),
  @JsonSubTypes.Type(value = CompanyResponse.class, name = "COMPANY")
})
public sealed interface ClientResponse permits PersonResponse, CompanyResponse {

  UUID id();

  String name();

  String email();

  String phone();

  List<ContractSummary> contracts();

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
  Instant createdAt();


  record ContractSummary(UUID id, String name, boolean active) {}
}
