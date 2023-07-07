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
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.eq;
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
    void addAddress_ValidAddressDto_ReturnsCreatedResponse() throws AuthenticationFailException {
        // Arrange
        AddressDto addressDto = new AddressDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.addAddress(addressDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Address added successfully", response.getMessage());

        ArgumentCaptor<AddressDto> addressDtoArgumentCaptor = ArgumentCaptor.forClass(AddressDto.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(addressService).saveAddress(addressDtoArgumentCaptor.capture(), userArgumentCaptor.capture());
        assertEquals(addressDto, addressDtoArgumentCaptor.getValue());
        assertEquals(user, userArgumentCaptor.getValue());
    }

    @Test
    void updateAddress_ValidAddressIdAndAddressDto_ReturnsOkResponse() throws AuthenticationFailException {
        // Arrange
        Long addressId = 1L;
        AddressDto addressDto = new AddressDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Address address = new Address();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);
        when(addressService.readAddress(eq(addressId))).thenReturn(Optional.of(address));
        when(addressService.readAddress(eq(user), eq(addressId))).thenReturn(address);

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.updateAddress(addressId, addressDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Address updated successfully", response.getMessage());

        ArgumentCaptor<AddressDto> addressDtoArgumentCaptor = ArgumentCaptor.forClass(AddressDto.class);
        ArgumentCaptor<Long> addressIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(addressService).updateAddress(addressDtoArgumentCaptor.capture(), addressIdArgumentCaptor.capture(), userArgumentCaptor.capture());
        assertEquals(addressDto, addressDtoArgumentCaptor.getValue());
        assertEquals(addressId, addressIdArgumentCaptor.getValue());
        assertEquals(user, userArgumentCaptor.getValue());
    }

    @Test
    void updateAddress_InvalidAddressId_ReturnsNotFoundResponse() throws AuthenticationFailException {
        // Arrange
        Long addressId = 1L;
        AddressDto addressDto = new AddressDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);
        when(addressService.readAddress(eq(addressId))).thenReturn(Optional.empty());

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
    void updateAddress_AddressNotBelongToCurrentUser_ReturnsForbiddenResponse() throws AuthenticationFailException {
        // Arrange
        Long addressId = 1L;
        AddressDto addressDto = new AddressDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Address address = new Address();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);
        when(addressService.readAddress(eq(addressId))).thenReturn(Optional.of(address));
        when(addressService.readAddress(eq(user), eq(addressId))).thenReturn(null);

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
    void listAddress_ReturnsListOfAddresses() throws AuthenticationFailException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<Address> addressList = new ArrayList<>();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);
        when(addressService.listAddress(eq(user))).thenReturn(addressList);

        // Act
        ResponseEntity<List<Address>> responseEntity = addressController.listAddress(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Address> body = responseEntity.getBody();
        assertEquals(addressList, body);
    }

    @Test
    void deleteAddress_ValidAddressId_ReturnsOkResponse() throws AuthenticationFailException {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Address address = new Address();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);
        when(addressService.readAddress(eq(addressId))).thenReturn(Optional.of(address));
        when(addressService.readAddress(eq(user), eq(addressId))).thenReturn(address);

        // Act
        ResponseEntity<ApiResponse> responseEntity = addressController.deleteAddress(addressId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Address deleted successfully", response.getMessage());

        verify(addressService).deleteAddress(eq(addressId));
    }

    @Test
    void deleteAddress_InvalidAddressId_ReturnsNotFoundResponse() throws AuthenticationFailException {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);
        when(addressService.readAddress(eq(addressId))).thenReturn(Optional.empty());

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
    void deleteAddress_AddressNotBelongToCurrentUser_ReturnsForbiddenResponse() throws AuthenticationFailException {
        // Arrange
        Long addressId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Address address = new Address();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);
        when(addressService.readAddress(eq(addressId))).thenReturn(Optional.of(address));
        when(addressService.readAddress(eq(user), eq(addressId))).thenReturn(null);

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
        Address address = new Address();
        when(addressService.readAddress(eq(addressId))).thenReturn(Optional.of(address));

        // Act
        ResponseEntity<Address> responseEntity = addressController.getAddressById(addressId);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Address resultAddress = responseEntity.getBody();
        assertNotNull(resultAddress);
        assertEquals(address, resultAddress);
    }

    @Test
    void getAddressById_NonExistingAddressId_ReturnsNotFoundResponse() {
        // Arrange
        Long addressId = 1L;
        when(addressService.readAddress(eq(addressId))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Address> responseEntity = addressController.getAddressById(addressId);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
}
