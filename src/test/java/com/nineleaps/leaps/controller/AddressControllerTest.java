package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.service.AddressServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
class AddressControllerTest {

    @Mock
    private AddressServiceInterface addressService;
    @Mock
    private Helper helper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AddressController addressController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Add Address - Success")
    void addAddress_ValidAddressDto_ReturnsCreatedResponse() {
        // Arrange
        AddressDto addressDto = new AddressDto();
        HttpServletRequest request = mock(HttpServletRequest.class);

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
    @DisplayName("List Address - Success")
    void listAddress_ReturnsListOfAddresses() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        List<Address> addressList = new ArrayList<>();

        when(addressService.listAddress((request))).thenReturn(addressList);

        // Act
        ResponseEntity<List<Address>> responseEntity = addressController.listAddress(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Address> body = responseEntity.getBody();
        assertEquals(addressList, body);
    }

    @Test
    @DisplayName("Delete Address - Success")
    void deleteAddress_ValidAddressId_ReturnsOkResponse() {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        Address address = new Address();

        when(addressService.readAddress((request), (addressId))).thenReturn(address);

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.deleteAddress(addressId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Address deleted successfully", response.getMessage());

        verify(addressService).deleteAddress((request), (addressId));
    }

    @Test
    @DisplayName("Get Address - Success")
    void getAddressById_ExistingAddressId_ReturnsAddress() {
        // Arrange
        Long addressId = 1L;
        Address address = new Address();

        // Mock the behavior of addressService.readAddress(user, addressId) to indicate that the address belongs to the user
        when(addressService.readAddress(request, addressId)).thenReturn(address);

        // Act
        ResponseEntity<Address> responseEntity = addressController.getAddressById(addressId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Address resultAddress = responseEntity.getBody();
        assertNotNull(resultAddress);
        assertEquals(address, resultAddress);

        // Verify that the relevant methods were called with the expected arguments
        verify(addressService).readAddress(request, addressId);
    }
}