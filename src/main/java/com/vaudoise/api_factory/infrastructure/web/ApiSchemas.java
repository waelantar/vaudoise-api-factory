package com.vaudoise.api_factory.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;

/** Common API schema definitions following Vaudoise API Guidelines. */
public class ApiSchemas {

  /** RFC 7807 Problem Detail error response. */
  @Schema(name = "ProblemDetail", description = "RFC 7807 Problem Details for HTTP APIs")
  public static class ProblemDetailSchema {

    @Schema(
        description = "A URI reference that identifies the problem type",
        example = "https://api.vaudoise.ch/problems/resource-not-found")
    public String type;

    @Schema(
        description = "A short, human-readable summary of the problem type",
        example = "Resource Not Found")
    public String title;

    @Schema(description = "The HTTP status code", example = "404")
    public Integer status;

    @Schema(
        description = "A human-readable explanation specific to this occurrence",
        example = "Client with id 123 not found")
    public String detail;

    @Schema(
        description = "A URI reference that identifies the specific occurrence",
        example = "/api/v1/clients/123")
    public String instance;

    @Schema(description = "Timestamp of when the error occurred", example = "2025-10-14T10:30:00Z")
    public String timestamp;
  }

  /** Validation error response with violations. */
  @Schema(name = "ValidationError", description = "Validation error response with field violations")
  public static class ValidationErrorSchema extends ProblemDetailSchema {

    @Schema(description = "List of validation violations")
    public java.util.List<Violation> violations;
  }

  @Schema(name = "Violation", description = "Field validation violation")
  public static class Violation {

    @Schema(description = "The field that failed validation", example = "email")
    public String field;

    @Schema(description = "The validation error message", example = "must be a valid email address")
    public String message;
  }
}
