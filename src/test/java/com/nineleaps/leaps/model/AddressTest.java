package com.nineleaps.leaps.model;

import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.enums.AddressType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTest {

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address();
    }

    @Test
    void getId() {
        address.setId(1L);
        assertEquals(1L, address.getId());
    }

    @Test
    void getAddressType() {
        address.setAddressType(AddressType.HOME);
        assertEquals(AddressType.HOME, address.getAddressType());
    }

    @Test
    void getAddressLine1() {
        address.setAddressLine1("123 Main St");
        assertEquals("123 Main St", address.getAddressLine1());
    }

    @Test
    void getAddressLine2() {
        address.setAddressLine2("Apt 4B");
        assertEquals("Apt 4B", address.getAddressLine2());
    }

    @Test
    void getCity() {
        address.setCity("New York");
        assertEquals("New York", address.getCity());
    }

    @Test
    void getState() {
        address.setState("NY");
        assertEquals("NY", address.getState());
    }

    @Test
    void getPostalCode() {
        address.setPostalCode("10001");
        assertEquals("10001", address.getPostalCode());
    }

    @Test
    void getCountry() {
        address.setCountry("USA");
        assertEquals("USA", address.getCountry());
    }

    @Test
    void isDefaultAddress() {
        address.setDefaultAddress(true);
        assertEquals(true, address.isDefaultAddress());
    }

    @Test
    void getUser() {
        User user = new User();
        address.setUser(user);
        assertEquals(user, address.getUser());
    }

    @Test
    void setId() {
        address.setId(2L);
        assertEquals(2L, address.getId());
    }

    @Test
    void setAddressType() {
        address.setAddressType(AddressType.OFFICE);
        assertEquals(AddressType.OFFICE, address.getAddressType());
    }

    @Test
    void setAddressLine1() {
        address.setAddressLine1("456 Elm St");
        assertEquals("456 Elm St", address.getAddressLine1());
    }

    @Test
    void setAddressLine2() {
        address.setAddressLine2("Suite 200");
        assertEquals("Suite 200", address.getAddressLine2());
    }

    @Test
    void setCity() {
        address.setCity("Los Angeles");
        assertEquals("Los Angeles", address.getCity());
    }

    @Test
    void setState() {
        address.setState("CA");
        assertEquals("CA", address.getState());
    }

    @Test
    void setPostalCode() {
        address.setPostalCode("90001");
        assertEquals("90001", address.getPostalCode());
    }

    @Test
    void setCountry() {
        address.setCountry("Canada");
        assertEquals("Canada", address.getCountry());
    }

    @Test
    void setDefaultAddress() {
        address.setDefaultAddress(false);
        assertEquals(false, address.isDefaultAddress());
    }

    @Test
    void setUser() {
        User user = new User();
        address.setUser(user);
        assertEquals(user, address.getUser());
    }

    @Test
    void constructorWithAddress() {
        Address sourceAddress = new Address();
        sourceAddress.setAddressType(AddressType.OFFICE);
        sourceAddress.setAddressLine1("789 Oak St");
        sourceAddress.setCity("San Francisco");
        sourceAddress.setState("CA");
        sourceAddress.setPostalCode("94101");
        sourceAddress.setCountry("USA");
        sourceAddress.setDefaultAddress(true);

        User user = new User();
        address = new Address(sourceAddress, user);

        assertEquals(AddressType.OFFICE, address.getAddressType());
        assertEquals("789 Oak St", address.getAddressLine1());
        assertEquals("San Francisco", address.getCity());
        assertEquals("CA", address.getState());
        assertEquals("94101", address.getPostalCode());
        assertEquals("USA", address.getCountry());
        assertEquals(true, address.isDefaultAddress());
        assertEquals(user, address.getUser());
    }

    @Test
    void constructorWithAddressDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setAddressType(AddressType.HOME);
        addressDto.setAddressLine1("555 Elm St");
        addressDto.setCity("Chicago");
        addressDto.setState("IL");
        addressDto.setPostalCode("60601");
        addressDto.setCountry("USA");
        addressDto.setDefaultAddress(false);

        User user = new User();
        address = new Address(addressDto, user);

        assertEquals(AddressType.HOME, address.getAddressType());
        assertEquals("555 Elm St", address.getAddressLine1());
        assertEquals("Chicago", address.getCity());
        assertEquals("IL", address.getState());
        assertEquals("60601", address.getPostalCode());
        assertEquals("USA", address.getCountry());
        assertEquals(false, address.isDefaultAddress());
        assertEquals(user, address.getUser());
    }

    @Test
    void constructorWithAddressId() {
        User user = new User();
        address = new Address(3L, user);
        assertEquals(3L, address.getId());
        assertEquals(user, address.getUser());
    }
}
