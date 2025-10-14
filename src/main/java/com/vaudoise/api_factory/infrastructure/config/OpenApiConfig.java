package com.vaudoise.api_factory.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** OpenAPI configuration for Vaudoise API Factory. Follows Vaudoise API Guidelines. */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI vaudoiseOpenAPI() {
    return new OpenAPI().info(apiInfo()).servers(servers()).tags(tags());
  }

  private Info apiInfo() {
    return new Info()
        .title("Vaudoise API Factory - Client & Contract Management")
        .description(
            """
                    RESTful API for managing insurance clients and contracts.

                    ## Features
                    - Client management (Person and Company)
                    - Contract lifecycle management
                    - Active contract filtering
                    - Performance-optimized cost calculation

                    ## Standards
                    - Follows Vaudoise API Guidelines
                    - RFC 7807 Problem Details for error responses
                    - ISO 8601 date format
                    - E.164 phone number format
                    """)
        .version("1.0.0")
        .contact(
            new Contact()
                .name("La Vaudoise - API Factory Team")
                .email("api-factory@vaudoise.ch")
                .url("https://www.vaudoise.ch"))
        .license(new License().name("Proprietary").url("https://www.vaudoise.ch/terms"));
  }

  private List<Server> servers() {
    return List.of(
        new Server().url("http://localhost:8080").description("Local Development Server"),
        new Server()
            .url("http://localhost:8080/api/v1")
            .description("Local Development Server with Base Path"));
  }

  private List<Tag> tags() {
    return List.of(
        new Tag()
            .name("Clients")
            .description("Operations for managing clients (Person and Company)"),
        new Tag().name("Contracts").description("Operations for managing insurance contracts"),
        new Tag().name("Health").description("Health check and monitoring endpoints"));
  }
}
