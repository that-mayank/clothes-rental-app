//package com.nineleaps.leaps.controller;
//
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.common.ApiResponse;
//import com.nineleaps.leaps.dto.AddressDto;
//import com.nineleaps.leaps.exceptions.AuthenticationFailException;
//import com.nineleaps.leaps.model.Address;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.service.AddressServiceInterface;
//import com.nineleaps.leaps.utils.Helper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@Tag("unit_tests")
//@DisplayName("Address controller test file")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//class AddressControllerTest {
//
//    @Mock
//    private AddressServiceInterface addressService;
//
//    @Mock
//    private Helper helper;
//
//    @InjectMocks
//    private AddressController addressController;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @BeforeEach
//    void setUp() {
//        // Setup mocks
//        // Initialize mocks and inject them into the controller
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("Add address")
//    void addAddress() throws AuthenticationFailException {
//        // Mock user and addressDto
//        User user = new User();
//        AddressDto addressDto = new AddressDto();
//
//        // Mock behavior
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        doNothing().when(addressService).saveAddress(any(), any());
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.addAddress(addressDto, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
//
//    }
//
//
//
//    @Test
//    @DisplayName("Add address - Exception Case")
//    void addAddressExceptionCase() throws AuthenticationFailException {
//        // Mock user and addressDto
//        User user = new User();
//        AddressDto addressDto = new AddressDto();
//
//        // Mock behavior to throw an exception when addressService.saveAddress is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.addAddress(addressDto, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
//    }
//
//
//    @Test
//    @DisplayName("Update address")
//    void updateAddress() throws AuthenticationFailException {
//        // Mock user and addressDto
//        User user = new User();
//        AddressDto addressDto = new AddressDto();
//        Long addressId = 1L;
//
//        // Mock behavior
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
//        when(addressService.readAddress(any(), anyLong())).thenReturn(new Address());
//        doNothing().when(addressService).updateAddress(any(), anyLong(), any());
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.updateAddress(addressId, addressDto, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
//    }
//
//    @Test
//    @DisplayName("Update address with valid address ID")
//    void updateAddress_ValidAddressId_AddressUpdatedSuccessfully() throws AuthenticationFailException {
//        // Mock user and addressDto
//        User user = new User();
//        AddressDto addressDto = new AddressDto();
//        Long addressId = 1L;
//
//        // Mock behavior
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
//        when(addressService.readAddress(any(), anyLong())).thenReturn(new Address());
//        doNothing().when(addressService).updateAddress(any(), anyLong(), any());
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.updateAddress(addressId, addressDto, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
//    }
//
//    @Test
//    @DisplayName("Update address with Invalid Address ID")
//    void updateAddress_InvalidAddressId_ReturnsNotFound() throws AuthenticationFailException {
//        // Mock user and addressDto
//        User user = new User();
//        AddressDto addressDto = new AddressDto();
//        Long addressId = 1L;
//
//        // Mock behavior for invalid addressId
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.readAddress(anyLong())).thenReturn(Optional.empty());
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.updateAddress(addressId, addressDto, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
//    }
//
//    @Test
//    @DisplayName("Try Update address return forbidden")
//    void updateAddress_AddressNotBelongToUser_ReturnsForbidden() throws AuthenticationFailException {
//        // Mock user and addressDto
//        User user = new User();
//        AddressDto addressDto = new AddressDto();
//        Long addressId = 1L;
//
//        // Mock behavior for address not belonging to user
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
//        when(addressService.readAddress(any(), anyLong())).thenReturn(null);
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.updateAddress(addressId, addressDto, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
//    }
//
//    @Test
//    @DisplayName("Update address - Exception Case")
//    void updateAddressExceptionCase() throws AuthenticationFailException {
//        // Mock addressId, user, and addressDto
//        Long addressId = 1L;
//        User user = new User();
//        user.setId(1L);
//        AddressDto addressDto = new AddressDto();
//        addressDto.setId(1L);
//        Address address = new Address(addressDto,user);
//
//        // Mock behavior to throw an exception when addressService.readAddress is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.readAddress(request,address.getId())).thenReturn(address);
//        when(addressService.readAddress(addressId)).thenReturn(Optional.of(address));
//
//        // Simulate an exception by throwing an exception within doAnswer
//        doAnswer(invocation -> {
//            throw new Exception("Simulated exception");
//        }).when(addressService).updateAddress(any(), any(), any());
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.updateAddress(addressId, addressDto, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
//    }
//
//
//
//    @Test
//    @DisplayName("list address")
//    void listAddress() throws AuthenticationFailException {
//        // Mock user
//        User user = new User();
//
//        // Mock behavior
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.listAddress(any())).thenReturn(new ArrayList<>());
//
//        // Call the method
//        ResponseEntity<List<Address>> response = addressController.listAddress(request);
//
//        // Assert the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
//    }
//
//    @Test
//    @DisplayName("List Address - Exception Case")
//    void listAddressExceptionCase() throws AuthenticationFailException {
//        // Mock user
//        User user = new User();
//
//        // Mock behavior to return an exception when addressService.listAddress is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        doAnswer(invocation -> {
//            throw new Exception("Simulated exception");
//        }).when(addressService).listAddress(request);
//
//        // Call the method
//        ResponseEntity<List<Address>> response = addressController.listAddress(request);
//
//        // Assert the response
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("delete address")
//    void deleteAddress() throws AuthenticationFailException {
//        // Mock user and addressId
//        User user = new User();
//        Long addressId = 1L;
//
//        // Mock behavior
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
//        when(addressService.readAddress(any(), anyLong())).thenReturn(new Address());
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.deleteAddress(addressId, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
//    }
//
//    @Test
//    @DisplayName("delete address with valid address ID")
//    void deleteAddress_ValidAddressId_AddressDeletedSuccessfully() throws AuthenticationFailException {
//        // Mock user
//        User user = new User();
//        Long addressId = 1L;
//
//        // Mock behavior
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
//        doNothing().when(addressService).deleteAddress(request,anyLong());
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.deleteAddress(addressId, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess(), "Address deleted successfully");
//    }
//
//
//
//
//    @Test
//    @DisplayName("delete address with invalid address ID")
//    void deleteAddress_InvalidAddressId_ReturnsNotFound() throws AuthenticationFailException {
//        // Mock user
//        User user = new User();
//        Long addressId = 1L;
//
//        // Mock behavior for invalid addressId
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.readAddress(anyLong())).thenReturn(Optional.empty());
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.deleteAddress(addressId, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
//    }
//
//    @Test
//    @DisplayName("delete address that doesnt belong to user return forbidden")
//    void deleteAddress_AddressNotBelongToUser_ReturnsNoContent() throws AuthenticationFailException {
//        // Mock user
//        User user = new User();
//        Long addressId = 1L;
//
//        // Mock behavior for address not belonging to user
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
//        when(addressService.readAddress(any(), anyLong())).thenReturn(null);
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.deleteAddress(addressId, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
//    }
//
//    @Test
//    @DisplayName("Delete Address - Exception Case")
//    void deleteAddressExceptionCase() throws AuthenticationFailException {
//        // Mock addressId, user, and addressDto
//        Long addressId = 1L;
//        User user = new User();
//        user.setId(1L);
//        AddressDto addressDto = new AddressDto();
//        addressDto.setId(1L);
//        Address address = new Address(addressDto,user);
//
//        // Mock behavior to throw an exception when addressService.readAddress is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(addressService.readAddress(request,address.getId())).thenReturn(address);
//        when(addressService.readAddress(addressId)).thenReturn(Optional.of(address));
//
//        doAnswer(invocation -> {
//            throw new Exception("Simulated exception");
//        }).when(addressService).deleteAddress(request,addressId);
//
//        // Call the method
//        ResponseEntity<ApiResponse> response = addressController.deleteAddress(addressId, request);
//
//        // Assert the response
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//
//    @Test
//    @DisplayName("get address by id")
//    void getAddressById() {
//        // Mock addressId
//        Long addressId = 1L;
//
//        // Mock behavior
//        when(addressService.readAddress(anyLong())).thenReturn(Optional.of(new Address()));
//
//        // Call the method
//        ResponseEntity<Address> response = addressController.getAddressById(addressId,request);
//
//        // Assert the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//    }
//
//    @Test
//    @DisplayName("get address detail by id")
//    void getAddressById_AddressFound_ReturnsAddress() {
//        // Mock data
//        Long addressId = 1L;
//        Address address = new Address(); // Create an address instance
//
//        // Mock behavior
//        when(addressService.readAddress(addressId)).thenReturn(Optional.of(address));
//
//        // Call the method
//        ResponseEntity<Address> response = addressController.getAddressById(addressId,request);
//
//        // Assert the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(address, response.getBody());
//    }
//
//    @Test
//    @DisplayName("get address returns not found")
//    void getAddressById_AddressNotFound_ReturnsNotFound() {
//        // Mock data
//        Long addressId = 1L;
//
//        // Mock behavior
//        when(addressService.readAddress(addressId)).thenReturn(Optional.empty());
//
//        // Call the method
//        ResponseEntity<Address> response = addressController.getAddressById(addressId,request);
//
//        // Assert the response
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("Get Address by ID - Exception Case")
//    void getAddressByIdExceptionCase() {
//        // Mock addressId
//        Long addressId = 1L;
//
//        // Mock behavior to return an exception when addressService.readAddress is called
//        doAnswer(invocation -> {
//            throw new Exception("Simulated exception");
//        }).when(addressService).readAddress(addressId);
//
//        // Call the method
//        ResponseEntity<Address> response = addressController.getAddressById(addressId,request);
//
//        // Assert the response
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//}



package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.RuntimeBenchmarkExtension;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.AddressDto;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.service.AddressServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@Tag("unit_tests")
@DisplayName("Address controller test file")
@ExtendWith(RuntimeBenchmarkExtension.class)
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
