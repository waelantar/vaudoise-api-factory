package com.vaudoise.api_factory.domain;

import com.vaudoise.api_factory.domain.model.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "+41791234567",
            "+1234567890",
            "+4179123456789",
            "41791234567"  // Will be normalized to +41791234567
    })
    void shouldCreateValidPhoneNumber(String valid) {
        PhoneNumber phone = new PhoneNumber(valid);

        assertNotNull(phone.value());
        assertTrue(phone.value().startsWith("+"));
    }

    @Test
    void shouldNormalizePhoneNumber() {
        PhoneNumber phone1 = new PhoneNumber("41 79 123 45 67");
        PhoneNumber phone2 = new PhoneNumber("+41-79-123-45-67");
        PhoneNumber phone3 = new PhoneNumber("(41) 791234567");

        assertEquals("+41791234567", phone1.value());
        assertEquals("+41791234567", phone2.value());
        assertEquals("+41791234567", phone3.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+",
            "abc",
            "+041791234567",  // Leading zero after +
            ""
    })
    void shouldRejectInvalidPhoneNumbers(String invalid) {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber(invalid));
    }

    @Test
    void shouldRejectNullPhoneNumber() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber(null));
    }

    @Test
    void shouldExtractCountryCode() {
        PhoneNumber phone = new PhoneNumber("+41791234567");

        assertEquals("417", phone.getCountryCode());
    }

    @Test
    void shouldFormatPhoneNumber() {
        PhoneNumber phone = new PhoneNumber("+41791234567");
        String formatted = phone.format();

        assertTrue(formatted.contains("+41"));
        assertTrue(formatted.contains(" "));  // Has spaces
    }

    @Test
    void shouldImplementEquality() {
        PhoneNumber phone1 = new PhoneNumber("+41791234567");
        PhoneNumber phone2 = new PhoneNumber("41791234567");
        PhoneNumber phone3 = new PhoneNumber("+41791234568");

        assertEquals(phone1, phone2);
        assertEquals(phone1.hashCode(), phone2.hashCode());
        assertNotEquals(phone1, phone3);
    }
}