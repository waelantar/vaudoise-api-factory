package com.vaudoise.api_factory.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBusinessRuleException extends RuntimeException {

  public InvalidBusinessRuleException(String message) {
    super(message);
  }

  public InvalidBusinessRuleException(String message, Throwable cause) {
    super(message, cause);
  }
}
