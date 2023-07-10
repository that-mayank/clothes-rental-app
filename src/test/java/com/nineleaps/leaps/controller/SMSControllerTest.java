package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class SMSControllerTest {

    @Mock
    private SmsServiceInterface smsServiceInterface;

    @Mock
    private UserServiceInterface userService;

    @Mock
    private SimpMessagingTemplate webSocket;

    @InjectMocks
    private SMSController smsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void smsSubmit_ValidPhoneNumber_ReturnsSuccessResponse() {
        // Arrange
        String phoneNumber = "9066650446";
        when(userService.getUserViaPhoneNumber(phoneNumber)).thenReturn(someUserObject());

        // Act
        ResponseEntity<ApiResponse> response = smsController.smsSubmit(phoneNumber);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        // Add more assertions for the response body if necessary

        verify(smsServiceInterface, times(1)).send(phoneNumber);
        verify(webSocket, times(1)).convertAndSend(anyString(), anyString());
    }

    @Test
     void smsSubmit_PhoneNumberNotPresentInDatabase_ReturnsNotFoundResponse() {
        // Arrange
        String phoneNumber = "1234567890";
        when(userService.getUserViaPhoneNumber(phoneNumber)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> response = smsController.smsSubmit(phoneNumber);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Phone number not present in database", response.getBody().getMessage());

        // Add more assertions for the response body if necessary

        verify(smsServiceInterface, never()).send(phoneNumber);
        verify(webSocket, never()).convertAndSend(anyString(), anyString());
    }


    @Test
     void verifyOTP_ValidOTP_ReturnsSuccessResponse() throws OtpValidationException, IOException {
        // Arrange
        String phoneNumber = "9066650446";
        int otp = 123456;
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Act
        ResponseEntity<ApiResponse> apiResponse = smsController.verifyOTP(response, request, phoneNumber, otp);

        // Assert
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
        // Add more assertions for the response body if necessary

        verify(smsServiceInterface, times(1)).verifyOtp(phoneNumber, otp, response, request);
    }



    private User someUserObject() {
        // Create and return a dummy User object for testing
        User user = new User();
        user.setFirstName("prath");
        user.setEmail("prath@gmail.com");
        user.setPhoneNumber("9066650446");
        user.setRole(Role.OWNER);

        return user;
    }


}