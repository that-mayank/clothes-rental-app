package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.RuntimeBenchmarkExtension;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.exceptions.AddressOwnershipException;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.AddressRepository;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
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

    @Mock
    private HttpServletRequest request;
    @Mock
    private Helper helper;


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
        User user = new User(); // create a user instance
        when(helper.getUserFromToken(request)).thenReturn(user);


        Address oldDefaultAddress1 = new Address();
        oldDefaultAddress1.setDefaultAddress(true);
        Address oldDefaultAddress2 = new Address();
        oldDefaultAddress2.setDefaultAddress(true);
        List<Address> oldAddresses = List.of(oldDefaultAddress1, oldDefaultAddress2);

        when(addressRepository.findAllByUser(user)).thenReturn(oldAddresses);

        // Act
        addressService.saveAddress(addressDto, request);

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
        User user = new User(); // create a user instance
        when(helper.getUserFromToken(request)).thenReturn(user);

        Address oldDefaultAddress1 = new Address();
        oldDefaultAddress1.setDefaultAddress(true);
        List<Address> oldAddresses = List.of(oldDefaultAddress1);

        when(addressRepository.findAllByUser(user)).thenReturn(oldAddresses);

        // Act
        addressService.saveAddress(addressDto, request);

        // Assert
        assertTrue(oldDefaultAddress1.isDefaultAddress());
    }

    @Test
    @DisplayName("Save Address: Save New Address - Address Saved Correctly")
    void saveAddress_SaveNewAddress_AddressSavedCorrectly() {
        // Arrange
        AddressDto addressDto = new AddressDto();
        addressDto.setDefaultAddress(false);
        User user = new User(); // create a user instance
        when(helper.getUserFromToken(request)).thenReturn(user);

        when(addressRepository.findAllByUser(user)).thenReturn(new ArrayList<>());
        when(addressRepository.save(any())).thenReturn(new Address());

        // Act
        addressService.saveAddress(addressDto, request);

        // Assert
        // Verify that save method is called
        verify(addressRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("List Address: Returns List of Addresses for User")
    void listAddress_ReturnsListOfAddressesForUser() {
        // Arrange
        User user = new User(); // create a user instance
        when(helper.getUserFromToken(request)).thenReturn(user);
        Address address1 = new Address();
        Address address2 = new Address();
        List<Address> expectedAddresses = List.of(address1, address2);

        when(addressRepository.findAllByUser(user)).thenReturn(expectedAddresses);

        // Act
        List<Address> actualAddresses = addressService.listAddress(request);

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
        User user = new User(); // create a user instance
        when(helper.getUserFromToken(request)).thenReturn(user);
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
        User user = new User(); // create a user instance
        when(helper.getUserFromToken(request)).thenReturn(user);
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
        User user = new User(); // create a user instance
        when(helper.getUserFromToken(request)).thenReturn(user);
        Address expectedAddress = new Address();
        expectedAddress.setId(addressId);
        List<Address> userAddresses = List.of(expectedAddress);

        when(addressRepository.findAllByUser(user)).thenReturn(userAddresses);

        // Act
        Address actualAddress = addressService.readAddress(request, addressId);

        // Assert
        assertNotNull(actualAddress);
        assertEquals(expectedAddress, actualAddress);
    }

    @Test
    @DisplayName("read address does not exist")
    void readAddress_AddressDoesNotExistForUser_ReturnsNull() {
        // Arrange
        Long addressId = 1L;
        User user = new User(); // create a user instance
        when(helper.getUserFromToken(request)).thenReturn(user);
        List<Address> userAddresses = new ArrayList<>();

        when(addressRepository.findAllByUser(user)).thenReturn(userAddresses);

        // Act
        Address actualAddress = addressService.readAddress(request, addressId);

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
        User user = new User(); // create a user instance
        when(helper.getUserFromToken(request)).thenReturn(user);
        // Act
        Address actualAddress = addressService.readAddress(request, addressId);

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
        User user = new User(); // create a user instance
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressRepository.findAllByUser(any())).thenReturn(userAddresses);

        // Act
        Address actualAddress = addressService.readAddress(request, addressId);

        // Assert
        assertNull(actualAddress);
    }






    @Test
    void updateAddress_AddressDoesNotBelongToCurrentUser_ShouldThrowException() {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(helper.getUserFromToken(request)).thenReturn(new User());
        when(addressRepository.findAllByUser(any())).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(AddressOwnershipException.class, () ->
                addressService.updateAddress(new AddressDto(), addressId, request));
    }

    @Test
    void updateAddress_Success() {
        //Arrange
        Long addressId = 1L;

        User user = new User();
        user.setId(1L);

        Address address = new Address();
        address.setId(1L);
        address.setUser(user);

        AddressDto addressDto = new AddressDto();

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressRepository.findAllByUser(any(User.class))).thenReturn(List.of(address));

        //Act
        addressService.updateAddress(addressDto, addressId, request);

        //Assert
        verify(addressRepository).save(any(Address.class));
    }


    @Test
    void deleteAddress_AddressDoesNotBelongToCurrentUser_ShouldThrowException() {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(helper.getUserFromToken(request)).thenReturn(new User());
        when(addressRepository.findAllByUser(any())).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(AddressOwnershipException.class, () ->
                addressService.deleteAddress(request, addressId));
    }

    @Test
    void deleteAddress_Success() {
        //Arrange
        Long addressId = 1L;

        User user = new User();
        user.setId(1L);

        Address address = new Address();
        address.setId(1L);
        address.setUser(user);

        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
        when(addressRepository.findAllByUser(any(User.class))).thenReturn(List.of(address));

        //Act
        addressService.deleteAddress(request, addressId);

        //Assert
        verify(addressRepository).deleteById(anyLong());
    }


}



