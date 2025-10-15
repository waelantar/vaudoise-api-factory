package com.vaudoise.api_factory.domain.model;

import java.util.Objects;

/**
 * Company client entity.
 */
public class Company extends Client {

    private CompanyIdentifier companyIdentifier;

    // For JPA/framework use
    protected Company() {
        super();
    }

    public Company(String name, Email email, PhoneNumber phone, CompanyIdentifier companyIdentifier) {
        super(name, email, phone);
        this.companyIdentifier = Objects.requireNonNull(
                companyIdentifier,
                "Company identifier cannot be null"
        );
    }

    @Override
    public ClientType getType() {
        return ClientType.COMPANY;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Company: %s (ID: %s)",
                getName(),
                companyIdentifier.value()
        );
    }

    // Getters
    public CompanyIdentifier getCompanyIdentifier() {
        return companyIdentifier;
    }

    // Note: companyIdentifier is immutable after creation (business rule)
}
