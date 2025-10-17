package com.vaudoise.api_factory.infrastructure.web.exception;

import com.vaudoise.api_factory.application.dto.response.ErrorResponse;
import com.vaudoise.api_factory.domain.exception.ClientNotFoundException;
import com.vaudoise.api_factory.domain.exception.DuplicateEmailException;
import com.vaudoise.api_factory.domain.exception.InvalidBusinessRuleException;
import com.vaudoise.api_factory.infrastructure.web.ApiSchemas;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmailException(
      DuplicateEmailException ex, WebRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            "about:blank",
            "Conflict",
            ex.getMessage(),
            HttpStatus.CONFLICT.value(),
            request.getDescription(false),
            Instant.now(),
            null);
    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ClientNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleClientNotFoundException(
      ClientNotFoundException ex, WebRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            "about:blank",
            "Not Found",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false),
            Instant.now(),
            null);
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidBusinessRuleException.class)
  public ResponseEntity<ErrorResponse> handleInvalidBusinessRuleException(
      InvalidBusinessRuleException ex, WebRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            "about:blank",
            "Bad Request",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false),
            Instant.now(),
            null);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {
    List<ApiSchemas.Violation> violations =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                error -> {
                  ApiSchemas.Violation violation = new ApiSchemas.Violation();
                  violation.field = error.getField();
                  violation.message = error.getDefaultMessage();
                  return violation;
                })
            .collect(Collectors.toList());

    ErrorResponse errorResponse =
        new ErrorResponse(
            "about:blank",
            "Validation Failed",
            "Input validation failed",
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false),
            Instant.now(),
            violations);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}
