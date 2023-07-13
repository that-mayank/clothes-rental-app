package com.nineleaps.leaps.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AddressTypeTest {

    @Test
    void testAddressTypeEnums() {
        // Verify the label values using assertions
        assertThat(AddressType.HOME.getLabel()).isEqualTo("Home Address");
        assertThat(AddressType.OFFICE.getLabel()).isEqualTo("Office Address");
    }
}
