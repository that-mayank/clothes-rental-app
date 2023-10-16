package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.exceptions.AddressOwnershipException;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.AddressServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressControllerTest {

    @Mock
    private AddressServiceInterface addressService;

    @Mock
    private Helper helper;

    @InjectMocks
    private AddressController addressController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addAddress_ValidAddressDto_ReturnsCreatedResponse() {
        // Arrange
        AddressDto addressDto = new AddressDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        when(helper.getUser((request))).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.addAddress(addressDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Address added successfully", response.getMessage());
    }

    @Test
    void updateAddress_ValidAddressIdAndAddressDto_ReturnsOkResponse() {
        // Arrange
        Long addressId = 1L;
        AddressDto addressDto = new AddressDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Address address = new Address();

        when(helper.getUser((request))).thenReturn(user);
        when(addressService.readAddress((addressId))).thenReturn(Optional.of(address));
        when(addressService.readAddress((user), (addressId))).thenReturn(address);

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.updateAddress(addressId, addressDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Address updated successfully", response.getMessage());
    }

    @Test
    void updateAddress_InvalidAddressId_ReturnsNotFoundResponse() {
        // Arrange
        Long addressId = 1L;
        AddressDto addressDto = new AddressDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        when(helper.getUser((request))).thenReturn(user);
        when(addressService.readAddress((addressId))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.updateAddress(addressId, addressDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Address not valid", response.getMessage());
    }

    @Test
    void updateAddress_AddressNotBelongToCurrentUser_ReturnsForbiddenResponse() {
        // Arrange
        Long addressId = 1L;
        AddressDto addressDto = new AddressDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Address address = new Address();

        when(helper.getUser((request))).thenReturn(user);
        when(addressService.readAddress((addressId))).thenReturn(Optional.of(address));
        when(addressService.readAddress((user), (addressId))).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.updateAddress(addressId, addressDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Address does not belong to current user", response.getMessage());
    }

    @Test
    void listAddress_ReturnsListOfAddresses() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<Address> addressList = new ArrayList<>();

        when(helper.getUser((request))).thenReturn(user);
        when(addressService.listAddress((user))).thenReturn(addressList);

        // Act
        ResponseEntity<List<Address>> responseEntity = addressController.listAddress(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Address> body = responseEntity.getBody();
        assertEquals(addressList, body);
    }

    @Test
    void deleteAddress_ValidAddressId_ReturnsOkResponse()  {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Address address = new Address();

        when(helper.getUser((request))).thenReturn(user);
        when(addressService.readAddress((addressId))).thenReturn(Optional.of(address));
        when(addressService.readAddress((user), (addressId))).thenReturn(address);

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.deleteAddress(addressId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Address deleted successfully", response.getMessage());

        verify(addressService).deleteAddress((addressId));
    }

    @Test
    void deleteAddress_InvalidAddressId_ReturnsNotFoundResponse()  {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        when(helper.getUser((request))).thenReturn(user);
        when(addressService.readAddress((addressId))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.deleteAddress(addressId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Address is invalid", response.getMessage());
    }

    @Test
    void deleteAddress_AddressNotBelongToCurrentUser_ReturnsForbiddenResponse() {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Address address = new Address();


        when(helper.getUser((request))).thenReturn(user);
        when(addressService.readAddress((addressId))).thenReturn(Optional.of(address));
        when(addressService.readAddress((user), (addressId))).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.deleteAddress(addressId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Address do not belong to the current user", response.getMessage());
    }

    @Test
    void getAddressById_ExistingAddressId_ReturnsAddress() {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Address address = new Address();

        when(helper.getUser((request))).thenReturn(user);

        // Mock the behavior of addressService.readAddress to return an optional address
        when(addressService.readAddress((addressId))).thenReturn(Optional.of(address));

        // Mock the behavior of addressService.readAddress(user, addressId) to indicate that the address belongs to the user
        when(addressService.readAddress(user, addressId)).thenReturn(address);

        // Act
        ResponseEntity<Address> responseEntity = addressController.getAddressById(addressId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Address resultAddress = responseEntity.getBody();
        assertNotNull(resultAddress);
        assertEquals(address, resultAddress);

        // Verify that the relevant methods were called with the expected arguments
        verify(helper).getUser(request);
        verify(addressService).readAddress(addressId);
        verify(addressService).readAddress(user, addressId);
    }

    @Test
    void getAddressById_NonExistingAddressId_ReturnsNotFoundResponse() {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        when(helper.getUser((request))).thenReturn(user);

        // Mock the behavior of addressService.readAddress to return an empty optional (address not found)
        when(addressService.readAddress(addressId)).thenReturn(Optional.empty());


        // Act
        ResponseEntity<Address> responseEntity = addressController.getAddressById(addressId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        // Verify that the relevant methods were called with the expected arguments
        verify(helper).getUser(request);
        verify(addressService).readAddress(addressId);
        verify(addressService, never()).readAddress(user, addressId);
    }

    @Test
    void getAddressById_AddressNotBelongingToUser_ThrowsAddressOwnershipException() {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Address address = new Address(); // Assume the address does not belong to the user

        when(helper.getUser(request)).thenReturn(user);

        // Mock the behavior of addressService.readAddress to return an optional address
        when(addressService.readAddress(addressId)).thenReturn(Optional.of(address));

        // Mock the behavior of addressService.readAddress(user, addressId) to indicate that the address does not belong to the user
        when(addressService.readAddress(user, addressId)).thenReturn(null);

        // Act and Assert (Exception)
        assertThrows(AddressOwnershipException.class, () -> addressController.getAddressById(addressId, request));

        // Verify that the relevant methods were called with the expected arguments
        verify(helper).getUser(request);
        verify(addressService).readAddress(addressId);
        verify(addressService).readAddress(user, addressId);
    }
}