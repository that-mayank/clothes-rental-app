package com.nineleaps.leaps.model;



import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.enums.AddressType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class AddressTest {

    @Test
     void testAddressGettersAndSetters() {
        // Create a sample User
        User user = new User();
        user.setId(1L);

        // Create an Address instance
        Address address = new Address();
        address.setId(1L);
        address.setAddressType(AddressType.HOME);
        address.setAddressLine1("123 Main St");
        address.setAddressLine2("Apt 4B");
        address.setCity("City");
        address.setState("State");
        address.setPostalCode("12345");
        address.setCountry("Country");
        address.setDefaultAddress(true);
        address.setUser(user);

        // Verify the getters
        assertEquals(1L, address.getId());
        assertEquals(AddressType.HOME, address.getAddressType());
        assertEquals("123 Main St", address.getAddressLine1());
        assertEquals("Apt 4B", address.getAddressLine2());
        assertEquals("City", address.getCity());
        assertEquals("State", address.getState());
        assertEquals("12345", address.getPostalCode());
        assertEquals("Country", address.getCountry());
        assertTrue(address.isDefaultAddress());
        assertEquals(user, address.getUser());

        // Modify some values using the setters
        address.setAddressLine1("456 Oak St");
        address.setDefaultAddress(false);

        // Verify the modified values
        assertEquals("456 Oak St", address.getAddressLine1());
        assertFalse(address.isDefaultAddress());
    }

    @Test
     void testAddressConstructors() {
        // Create a sample User
        User user = new User();
        user.setId(1L);

        // Create an Address instance using the copy constructor
        Address sourceAddress = new Address();
        sourceAddress.setId(1L);
        sourceAddress.setAddressType(AddressType.HOME);
        sourceAddress.setAddressLine1("789 Elm St");
        sourceAddress.setCity("City");
        sourceAddress.setState("State");
        sourceAddress.setPostalCode("67890");
        sourceAddress.setCountry("Country");
        sourceAddress.setDefaultAddress(true);

        Address addressCopy = new Address(sourceAddress, user);

        // Verify the values of the copied address
        assertEquals(sourceAddress.getAddressType(), addressCopy.getAddressType());
        assertEquals(sourceAddress.getAddressLine1(), addressCopy.getAddressLine1());
        assertEquals(sourceAddress.getCity(), addressCopy.getCity());
        assertEquals(sourceAddress.getState(), addressCopy.getState());
        assertEquals(sourceAddress.getPostalCode(), addressCopy.getPostalCode());
        assertEquals(sourceAddress.getCountry(), addressCopy.getCountry());
        assertEquals(sourceAddress.isDefaultAddress(), addressCopy.isDefaultAddress());
        assertEquals(user, addressCopy.getUser());

        // Create an Address instance using the DTO constructor
        AddressDto addressDto = new AddressDto();
        addressDto.setId(2L);
        addressDto.setAddressType(AddressType.HOME);
        addressDto.setAddressLine1("987 Pine St");
        addressDto.setCity("City");
        addressDto.setState("State");
        addressDto.setPostalCode("54321");
        addressDto.setCountry("Country");
        addressDto.setDefaultAddress(false);

        Address addressFromDto = new Address(addressDto, user);

        // Verify the values of the address created from the DTO
        assertEquals(addressDto.getId(), addressFromDto.getId());
        assertEquals(addressDto.getAddressType(), addressFromDto.getAddressType());
        assertEquals(addressDto.getAddressLine1(), addressFromDto.getAddressLine1());
        assertEquals(addressDto.getCity(), addressFromDto.getCity());
        assertEquals(addressDto.getState(), addressFromDto.getState());
        assertEquals(addressDto.getPostalCode(), addressFromDto.getPostalCode());
        assertEquals(addressDto.getCountry(), addressFromDto.getCountry());
        assertEquals(addressDto.isDefaultAddress(), addressFromDto.isDefaultAddress());
        assertEquals(user, addressFromDto.getUser());
    }
}