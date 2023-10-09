package com.nineleaps.leaps.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
@DisplayName("AddressType Tests")
@Tag("unit_tests")
class AddressTypeTest {

    @Test
    @DisplayName("Test HOME AddressType label")
    void testAddressTypeLabel() {
        AddressType homeAddressType = AddressType.HOME;
        AddressType officeAddressType = AddressType.OFFICE;

        // Check the label for HOME address type
        assertEquals("Home Address", homeAddressType.getLabel());

        // Check the label for OFFICE address type
        assertEquals("Office Address", officeAddressType.getLabel());
    }
}
