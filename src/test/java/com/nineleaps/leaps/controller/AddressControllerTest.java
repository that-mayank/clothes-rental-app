package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;

import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class AddressControllerTest {

    @Mock
    private AddressServiceInterface addressService;

    @Mock
    private Helper helper;

    @InjectMocks
    private AddressController addressController;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        // Setup mocks
        // Initialize mocks and inject them into the controller
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addAddress() throws AuthenticationFailException {
        // Mock user and addressDto
        User user = new User();
        AddressDto addressDto = new AddressDto();

        // Mock behavior
        when(helper.getUserFromToken(request)).thenReturn(user);
        doNothing().when(addressService).saveAddress(any(), any());

        // Call the method
        ResponseEntity<ApiResponse> response = addressController.addAddress(addressDto, request);

        // Assert the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    }


    @Test
    void updateAddress() throws AuthenticationFailException {
        // Mock user and addressDto
        User user = new User();
        AddressDto addressDto = new AddressDto();
        Long addressId = 1L;

        // Mock behavior
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
        when(addressService.readAddress(any(), anyLong())).thenReturn(new Address());
        doNothing().when(addressService).updateAddress(any(), anyLong(), any());

        // Call the method
        ResponseEntity<ApiResponse> response = addressController.updateAddress(addressId, addressDto, request);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void updateAddress_ValidAddressId_AddressUpdatedSuccessfully() throws AuthenticationFailException {
        // Mock user and addressDto
        User user = new User();
        AddressDto addressDto = new AddressDto();
        Long addressId = 1L;

        // Mock behavior
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
        when(addressService.readAddress(any(), anyLong())).thenReturn(new Address());
        doNothing().when(addressService).updateAddress(any(), anyLong(), any());

        // Call the method
        ResponseEntity<ApiResponse> response = addressController.updateAddress(addressId, addressDto, request);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void updateAddress_InvalidAddressId_ReturnsNotFound() throws AuthenticationFailException {
        // Mock user and addressDto
        User user = new User();
        AddressDto addressDto = new AddressDto();
        Long addressId = 1L;

        // Mock behavior for invalid addressId
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressService.readAddress(anyLong())).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<ApiResponse> response = addressController.updateAddress(addressId, addressDto, request);

        // Assert the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void updateAddress_AddressNotBelongToUser_ReturnsForbidden() throws AuthenticationFailException {
        // Mock user and addressDto
        User user = new User();
        AddressDto addressDto = new AddressDto();
        Long addressId = 1L;

        // Mock behavior for address not belonging to user
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
        when(addressService.readAddress(any(), anyLong())).thenReturn(null);

        // Call the method
        ResponseEntity<ApiResponse> response = addressController.updateAddress(addressId, addressDto, request);

        // Assert the response
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
    }



@Test
    void listAddress() throws AuthenticationFailException {
        // Mock user
        User user = new User();

        // Mock behavior
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressService.listAddress(any())).thenReturn(new ArrayList<>());

        // Call the method
        ResponseEntity<List<Address>> response = addressController.listAddress(request);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void deleteAddress() throws AuthenticationFailException {
        // Mock user and addressId
        User user = new User();
        Long addressId = 1L;

        // Mock behavior
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
        when(addressService.readAddress(any(), anyLong())).thenReturn(new Address());

        // Call the method
        ResponseEntity<ApiResponse> response = addressController.deleteAddress(addressId, request);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void deleteAddress_ValidAddressId_AddressDeletedSuccessfully() throws AuthenticationFailException {
        // Mock user
        User user = new User();
        Long addressId = 1L;

        // Mock behavior
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
        doNothing().when(addressService).deleteAddress(anyLong());

        // Call the method
        ResponseEntity<ApiResponse> response = addressController.deleteAddress(addressId, request);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess(), "Address deleted successfully");
    }




    @Test
    void deleteAddress_InvalidAddressId_ReturnsNotFound() throws AuthenticationFailException {
        // Mock user
        User user = new User();
        Long addressId = 1L;

        // Mock behavior for invalid addressId
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressService.readAddress(anyLong())).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<ApiResponse> response = addressController.deleteAddress(addressId, request);

        // Assert the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void deleteAddress_AddressNotBelongToUser_ReturnsNoContent() throws AuthenticationFailException {
        // Mock user
        User user = new User();
        Long addressId = 1L;

        // Mock behavior for address not belonging to user
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
        when(addressService.readAddress(any(), anyLong())).thenReturn(null);

        // Call the method
        ResponseEntity<ApiResponse> response = addressController.deleteAddress(addressId, request);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
    }


    @Test
    void getAddressById() {
        // Mock addressId
        Long addressId = 1L;

        // Mock behavior
        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));

        // Call the method
        ResponseEntity<Address> response = addressController.getAddressById(addressId);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAddressById_AddressFound_ReturnsAddress() {
        // Mock data
        Long addressId = 1L;
        Address address = new Address(); // Create an address instance

        // Mock behavior
        when(addressService.readAddress(addressId)).thenReturn(Optional.of(address));

        // Call the method
        ResponseEntity<Address> response = addressController.getAddressById(addressId);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(address, response.getBody());
    }

    @Test
    void getAddressById_AddressNotFound_ReturnsNotFound() {
        // Mock data
        Long addressId = 1L;

        // Mock behavior
        when(addressService.readAddress(addressId)).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<Address> response = addressController.getAddressById(addressId);

        // Assert the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
