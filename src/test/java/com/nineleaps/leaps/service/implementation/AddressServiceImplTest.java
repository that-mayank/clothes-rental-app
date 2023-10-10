package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.RuntimeBenchmarkExtension;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@Tag("unit_tests")
@DisplayName("AddressServiceImpl Tests")
@ExtendWith(RuntimeBenchmarkExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save Address: Default Address Set - Old Default Addresses Updated")
    void saveAddress_DefaultAddressSet_OldDefaultAddressesUpdated() {
        // Arrange
        AddressDto addressDto = new AddressDto();
        addressDto.setDefaultAddress(true);
        User user = new User();

        Address oldDefaultAddress1 = new Address();
        oldDefaultAddress1.setDefaultAddress(true);
        Address oldDefaultAddress2 = new Address();
        oldDefaultAddress2.setDefaultAddress(true);
        List<Address> oldAddresses = List.of(oldDefaultAddress1, oldDefaultAddress2);

        when(addressRepository.findAllByUser(user)).thenReturn(oldAddresses);

        // Act
        addressService.saveAddress(addressDto, user);

        // Assert
        assertFalse(oldDefaultAddress1.isDefaultAddress());
        assertFalse(oldDefaultAddress2.isDefaultAddress());
    }

    @Test
    @DisplayName("Save Address: Non-Default Address - No Update to Old Default Addresses")
    void saveAddress_NonDefaultAddress_NoUpdateToOldDefaultAddresses() {
        // Arrange
        AddressDto addressDto = new AddressDto();
        addressDto.setDefaultAddress(false);
        User user = new User();

        Address oldDefaultAddress1 = new Address();
        oldDefaultAddress1.setDefaultAddress(true);
        List<Address> oldAddresses = List.of(oldDefaultAddress1);

        when(addressRepository.findAllByUser(user)).thenReturn(oldAddresses);

        // Act
        addressService.saveAddress(addressDto, user);

        // Assert
        assertTrue(oldDefaultAddress1.isDefaultAddress());
    }

    @Test
    @DisplayName("Save Address: Save New Address - Address Saved Correctly")
    void saveAddress_SaveNewAddress_AddressSavedCorrectly() {
        // Arrange
        AddressDto addressDto = new AddressDto();
        addressDto.setDefaultAddress(false);
        User user = new User();

        when(addressRepository.findAllByUser(user)).thenReturn(new ArrayList<>());
        when(addressRepository.save(any())).thenReturn(new Address());

        // Act
        addressService.saveAddress(addressDto, user);

        // Assert
        // Verify that save method is called
        verify(addressRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("List Address: Returns List of Addresses for User")
    void listAddress_ReturnsListOfAddressesForUser() {
        // Arrange
        User user = new User();
        Address address1 = new Address();
        Address address2 = new Address();
        List<Address> expectedAddresses = List.of(address1, address2);

        when(addressRepository.findAllByUser(user)).thenReturn(expectedAddresses);

        // Act
        List<Address> actualAddresses = addressService.listAddress(user);

        // Assert
        assertEquals(expectedAddresses.size(), actualAddresses.size());
        assertTrue(actualAddresses.contains(address1));
        assertTrue(actualAddresses.contains(address2));
    }

    @Test
    @DisplayName("Read Address: Address Exists - Returns Address")
    void readAddress_AddressExists_ReturnsAddress() {
        // Arrange
        Long addressId = 1L;
        Address expectedAddress = new Address();
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(expectedAddress));

        // Act
        Optional<Address> actualAddress = addressService.readAddress(addressId);

        // Assert
        assertTrue(actualAddress.isPresent());
        assertEquals(expectedAddress, actualAddress.get());
    }

    @Test
    @DisplayName("Read Address: Address Does not  Exists - Returns Address")
    void readAddress_AddressDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        Long addressId = 1L;
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act
        Optional<Address> actualAddress = addressService.readAddress(addressId);

        // Assert
        assertFalse(actualAddress.isPresent());
    }

    @Test
    @DisplayName("read address")
    void readAddress_AddressExistsForUser_ReturnsAddress() {
        // Arrange
        Long addressId = 1L;
        User user = new User();
        Address expectedAddress = new Address();
        expectedAddress.setId(addressId);
        List<Address> userAddresses = List.of(expectedAddress);

        when(addressRepository.findAllByUser(user)).thenReturn(userAddresses);

        // Act
        Address actualAddress = addressService.readAddress(user, addressId);

        // Assert
        assertNotNull(actualAddress);
        assertEquals(expectedAddress, actualAddress);
    }

    @Test
    @DisplayName("read address does not exist")
    void readAddress_AddressDoesNotExistForUser_ReturnsNull() {
        // Arrange
        Long addressId = 1L;
        User user = new User();
        List<Address> userAddresses = new ArrayList<>();

        when(addressRepository.findAllByUser(user)).thenReturn(userAddresses);

        // Act
        Address actualAddress = addressService.readAddress(user, addressId);

        // Assert
        assertNull(actualAddress);
    }

    @Test
    @DisplayName("Read address via ID")
    void readAddress_AddressIdMatches_ReturnsAddress() {
        // Arrange
        Long addressId = 1L;
        Address expectedAddress = new Address();
        expectedAddress.setId(addressId);
        List<Address> userAddresses = List.of(expectedAddress);

        when(addressRepository.findAllByUser(any())).thenReturn(userAddresses);

        // Act
        Address actualAddress = addressService.readAddress(new User(), addressId);

        // Assert
        assertNotNull(actualAddress);
        assertEquals(expectedAddress, actualAddress);
    }

    @Test
    @DisplayName("Address ID Doesa not match")
    void readAddress_AddressIdDoesNotMatch_ReturnsNull() {
        // Arrange
        Long addressId = 1L;
        Address expectedAddress = new Address();
        expectedAddress.setId(2L);  // Different addressId
        List<Address> userAddresses = List.of(expectedAddress);

        when(addressRepository.findAllByUser(any())).thenReturn(userAddresses);

        // Act
        Address actualAddress = addressService.readAddress(new User(), addressId);

        // Assert
        assertNull(actualAddress);
    }





    @Test
    @DisplayName("Update address ")
    void updateAddress_ExistingAddressUpdated() {
        // Arrange
        Long addressId = 1L;
        AddressDto updatedAddressDto = new AddressDto();
        updatedAddressDto.setId(addressId);
        User user = new User();
        Address existingAddress = new Address(); // Mock an existing address
        existingAddress.setId(addressId);

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(existingAddress));

        // Act
        addressService.updateAddress(updatedAddressDto, addressId, user);

        // Assert
        verify(addressRepository, times(1)).save(any(Address.class));
    }


    @Test
    @DisplayName("Delete address")
    void deleteAddress_ValidAddressId_AddressDeleted() {
        // Arrange
        Long addressId = 1L;

        // Act
        addressService.deleteAddress(addressId);

        // Assert
        verify(addressRepository).deleteById(addressId);
    }




}
