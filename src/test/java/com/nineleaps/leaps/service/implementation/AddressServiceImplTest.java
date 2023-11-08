package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.AddressRepository;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AddressDto addressDto;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Helper helper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveAddress_shouldSaveAddress() {
        // Arrange
        AddressDto addressDto = new AddressDto();

        // Act
        addressService.saveAddress(addressDto, request);

        // Assert
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void saveAddress_withDefaultAddress() {
        // Prepare
        User user = new User();
        when(addressDto.isDefaultAddress()).thenReturn(true);
        List<Address> addresses = Arrays.asList(mock(Address.class), mock(Address.class));
        when(addressRepository.findAllByUser(user)).thenReturn(addresses);

        // Stub the behavior of the Address class constructor
        Address addressMock = mock(Address.class);
        doReturn(false).when(addressMock).isDefaultAddress(); // Stub the internal isDefaultAddress() call

        doReturn(addressMock).when(addressRepository).save(any(Address.class));

        // Execute
        addressService.saveAddress(addressDto, request);

        // Verify
        verify(addressDto, times(2)).isDefaultAddress();
        verify(addressRepository, times(1)).findAllByUser(user);
        for (Address address : addresses) {
            verify(address, times(1)).setDefaultAddress(false);
        }
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
        List<Address> result = addressService.listAddress(request);

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
        Address matchingAddress = new Address(addressId, user);
        addresses.add(matchingAddress);
        addresses.add(new Address());

        when(addressRepository.findAllByUser(user)).thenReturn(addresses);

        // Act
        Address result = addressService.readAddress(request, addressId);

        // Assert
        assertEquals(matchingAddress, result);
    }

    @Test
    void testReadAddress_shouldReturnNullWhenNoMatchingAddress() {
        // Arrange
        Long addressId = 1L;
        User user = new User();
        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address(2L, user));
        addresses.add(new Address(3L, user));

        when(addressRepository.findAllByUser(user)).thenReturn(addresses);

        // Act
        Address result = addressService.readAddress(request, addressId);

        // Assert
        assertNull(result);
    }

    @Test
    void updateAddress_shouldUpdateAddress() {
        // Arrange
        AddressDto addressDto = new AddressDto();
        Long addressId = 1L;
        User user = new User();

        // Act
        addressService.updateAddress(addressDto, addressId, request);

        // Assert
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void deleteAddress_shouldDeleteAddress() {
        // Arrange
        Long addressId = 1L;

        // Act
        addressService.deleteAddress(request, addressId);

        // Assert
        verify(addressRepository, times(1)).deleteById(addressId);
    }
}
