package com.vaudoise.api_factory.domain;

import com.vaudoise.api_factory.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompanyTest {

    @Test
    void shouldCreateCompanySuccessfully() {
        Email email = new Email("test@company.com");
        PhoneNumber phone = new PhoneNumber("+41791234567");
        CompanyIdentifier uid = new CompanyIdentifier("CHE-123.456.789");

        Company company = new Company("Test Company", email, phone, uid);

        assertThat(company.getName()).isEqualTo("Test Company");
        assertThat(company.getEmail()).isEqualTo(email);
        assertThat(company.getPhone()).isEqualTo(phone);
        assertThat(company.getCompanyIdentifier()).isEqualTo(uid);
        assertThat(company.getType()).isEqualTo(ClientType.COMPANY);
        assertThat(company.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        Email email = new Email("test@company.com");
        PhoneNumber phone = new PhoneNumber("+41791234567");
        CompanyIdentifier uid = new CompanyIdentifier("CHE-123.456.789");

        assertThatThrownBy(() -> new Company("  ", email, phone, uid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be empty");
    }

    @Test
    void shouldThrowExceptionWhenCompanyIdentifierIsNull() {
        Email email = new Email("test@company.com");
        PhoneNumber phone = new PhoneNumber("+41791234567");

        assertThatThrownBy(() -> new Company("Test Company", email, phone, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Company identifier cannot be null");
    }

    @Test
    void shouldUpdateInfoAndTimestamp() {
        Company company = new Company(
                "Old Name",
                new Email("old@company.com"),
                new PhoneNumber("+41791111111"),
                new CompanyIdentifier("CHE-111.111.111")
        );
        Instant originalUpdatedAt = company.getUpdatedAt();

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        company.updateInfo("New Name", new Email("new@company.com"), new PhoneNumber("+41792222222"));

        assertThat(company.getName()).isEqualTo("New Name");
        assertThat(company.getEmail().value()).isEqualTo("new@company.com");
        assertThat(company.getPhone().value()).isEqualTo("+41792222222");
        assertThat(company.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void shouldReturnCorrectDisplayInfo() {
        Company company = new Company(
                "MyCompany",
                new Email("test@my.com"),
                new PhoneNumber("+41791234567"),
                new CompanyIdentifier("CHE-999.888.777")
        );

        String displayInfo = company.getDisplayInfo();

        assertThat(displayInfo).isEqualTo("Company: MyCompany (ID: CHE-999.888.777)");
    }
}
