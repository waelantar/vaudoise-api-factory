package com.vaudoise.api_factory.domain;

import com.vaudoise.api_factory.domain.model.CompanyIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CompanyIdentifierTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "CHE-123.456.789",
            "CHE-000.000.001",
            "CHE-999.999.999"
    })
    void shouldCreateValidCompanyIdentifier(String valid) {
        CompanyIdentifier id = new CompanyIdentifier(valid);

        assertEquals(valid.toUpperCase(), id.value());
    }

    @Test
    void shouldNormalizeToUpperCase() {
        CompanyIdentifier id = new CompanyIdentifier("che-123.456.789");

        assertEquals("CHE-123.456.789", id.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123.456.789",
            "CHE123456789",
            "CHE-123456789",
            "CHE-12.456.789",
            "CHE-123.45.789",
            "CH-123.456.789",
            ""
    })
    void shouldRejectInvalidCompanyIdentifiers(String invalid) {
        assertThrows(IllegalArgumentException.class, () -> new CompanyIdentifier(invalid));
    }

    @Test
    void shouldRejectNull() {
        assertThrows(IllegalArgumentException.class, () -> new CompanyIdentifier(null));
    }

    @Test
    void shouldImplementEquality() {
        CompanyIdentifier id1 = new CompanyIdentifier("CHE-123.456.789");
        CompanyIdentifier id2 = new CompanyIdentifier("che-123.456.789");
        CompanyIdentifier id3 = new CompanyIdentifier("CHE-987.654.321");

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1, id3);
    }
}
