package com.vaudoise.api_factory.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

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
    assertThat(company.getContracts()).isEmpty();
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
  void shouldReturnCorrectDisplayInfo() {
    Company company =
        new Company(
            "MyCompany",
            new Email("test@my.com"),
            new PhoneNumber("+41791234567"),
            new CompanyIdentifier("CHE-999.888.777"));

    String displayInfo = company.getDisplayInfo();

    assertThat(displayInfo).isEqualTo("Company: MyCompany (ID: CHE-999.888.777)");
  }

  @Test
  void shouldAddContractAndBecomeActive() {
    Company company =
        new Company(
            "BizCorp",
            new Email("biz@corp.com"),
            new PhoneNumber("+41791234567"),
            new CompanyIdentifier("CHE-111.222.333"));
    Contract contract = new Contract(company, Money.chf(500.0));

    company.addContract(contract);

    assertThat(company.getContracts()).containsExactly(contract);
    assertThat(company.isActive()).isTrue();
  }

  @Test
  void shouldGetOnlyActiveContracts() {
    Company company =
        new Company(
            "BizCorp",
            new Email("biz@corp.com"),
            new PhoneNumber("+41791234567"),
            new CompanyIdentifier("CHE-111.222.333"));
    Contract activeContract = new Contract(company, Money.chf(500.0));
    Contract terminatedContract = new Contract(company, Money.chf(750.0));
    terminatedContract.terminate();
    company.addContract(activeContract);
    company.addContract(terminatedContract);

    List<Contract> activeContracts = company.getActiveContracts();

    assertThat(activeContracts).containsExactly(activeContract);
  }

  @Test
  void shouldTerminateAllContracts() {
    Company company =
        new Company(
            "BizCorp",
            new Email("biz@corp.com"),
            new PhoneNumber("+41791234567"),
            new CompanyIdentifier("CHE-111.222.333"));
    company.addContract(new Contract(company, Money.chf(500.0)));
    company.addContract(new Contract(company, Money.chf(750.0)));
    Instant originalUpdatedAt = company.getUpdatedAt();

    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    company.terminateAllContracts();

    assertThat(company.isActive()).isFalse();
    assertThat(company.getActiveContracts()).isEmpty();
    assertThat(company.getUpdatedAt()).isAfter(originalUpdatedAt);
  }
}
