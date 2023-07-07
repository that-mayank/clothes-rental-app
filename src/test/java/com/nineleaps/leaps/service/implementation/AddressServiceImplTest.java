package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveAddress_shouldSaveAddress() {
        // Arrange
        AddressDto addressDto = new AddressDto();
        User user = new User();

        // Act
        addressService.saveAddress(addressDto, user);

        // Assert
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void listAddress_shouldReturnListOfAddresses() {
        // Arrange
        User user = new User();
        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address());
        addresses.add(new Address());

        when(addressRepository.findAllByUser(user)).thenReturn(addresses);

        // Act
        List<Address> result = addressService.listAddress(user);

        // Assert
        assertEquals(2, result.size());
        assertEquals(addresses, result);
    }

    @Test
    void readAddress_shouldReturnOptionalAddress() {
        // Arrange
        Long addressId = 1L;
        Optional<Address> expectedAddress = Optional.of(new Address());

        when(addressRepository.findById(addressId)).thenReturn(expectedAddress);

        // Act
        Optional<Address> result = addressService.readAddress(addressId);

        // Assert
        assertEquals(expectedAddress, result);
    }

    @Test
    void testReadAddress_shouldReturnMatchingAddress() {
        // Arrange
        Long addressId = 1L;
        User user = new User();
        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address(addressId, user));
        addresses.add(new Address());

        when(addressRepository.findAllByUser(user)).thenReturn(addresses);

        // Act
        Address result = addressService.readAddress(user, addressId);

        // Assert
        assertEquals(addresses.get(0), result);
    }

    @Test
    void updateAddress_shouldUpdateAddress() {
        // Arrange
        AddressDto addressDto = new AddressDto();
        Long addressId = 1L;
        User user = new User();

        // Act
        addressService.updateAddress(addressDto, addressId, user);

        // Assert
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void deleteAddress_shouldDeleteAddress() {
        // Arrange
        Long addressId = 1L;

        // Act
        addressService.deleteAddress(addressId);

        // Assert
        verify(addressRepository, times(1)).deleteById(addressId);
    }
}
