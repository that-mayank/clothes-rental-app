package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.exceptions.AddressOwnershipException;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.AddressRepository;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private Helper helper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveAddress_DefaultAddressSet_ShouldRemoveDefaultFromOtherAddresses() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User(); // create a user instance
        when(helper.getUser(request)).thenReturn(user);

        AddressDto addressDto = new AddressDto();
        addressDto.setDefaultAddress(true);

        List<Address> addresses = new ArrayList<>();
        Address existingAddress = new Address();
        existingAddress.setDefaultAddress(true);
        addresses.add(existingAddress);
        when(addressRepository.findAllByUser(user)).thenReturn(addresses);

        // Act
        addressService.saveAddress(addressDto, request);

        // Assert
        assertFalse(existingAddress.isDefaultAddress());
        verify(addressRepository, times(1)).save(any());
    }

    @Test
    void listAddress_ShouldReturnListOfAddresses() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User(); // create a user instance
        when(helper.getUser(request)).thenReturn(user);

        List<Address> addresses = new ArrayList<>();
        when(addressRepository.findAllByUser(user)).thenReturn(addresses);

        // Act
        List<Address> result = addressService.listAddress(request);

        // Assert
        assertEquals(addresses, result);
    }

    @Test
    void readAddress_ExistingAddressId_ShouldReturnAddress() {
        // Arrange
        Long addressId = 1L;
        Address expectedAddress = new Address();
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(expectedAddress));

        // Act
        Optional<Address> result = addressService.readAddress(addressId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedAddress, result.get());
    }

    // Add more test cases for readAddress, updateAddress, and deleteAddress methods.

    @Test
    void updateAddress_AddressDoesNotBelongToCurrentUser_ShouldThrowException() {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(helper.getUser(request)).thenReturn(new User());
        when(addressRepository.findAllByUser(any())).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(AddressOwnershipException.class, () ->
                addressService.updateAddress(new AddressDto(), addressId, request));
    }

    @Test
    void deleteAddress_AddressDoesNotBelongToCurrentUser_ShouldThrowException() {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(helper.getUser(request)).thenReturn(new User());
        when(addressRepository.findAllByUser(any())).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(AddressOwnershipException.class, () ->
                addressService.deleteAddress(request, addressId));
    }
}
