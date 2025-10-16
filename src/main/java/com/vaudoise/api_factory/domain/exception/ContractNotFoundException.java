package com.vaudoise.api_factory.domain.exception;

public class ContractNotFoundException extends RuntimeException {
  public ContractNotFoundException(String message) {
    super(message);
  }
}
