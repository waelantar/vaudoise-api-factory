package com.vaudoise.api_factory.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COMPANY")
public class CompanyEntity extends ClientEntity {

  @Column(name = "company_identifier", unique = true)
  private String companyIdentifier;

  public String getCompanyIdentifier() {
    return companyIdentifier;
  }

  public void setCompanyIdentifier(String companyIdentifier) {
    this.companyIdentifier = companyIdentifier;
  }
}
