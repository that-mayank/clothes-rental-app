package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SMSControllerTest {
    @Mock
    private SmsServiceInterface smsService;
    @Mock
    private UserServiceInterface userService;
    @Mock
    private SimpMessagingTemplate webSocket;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse httpresponse;

    private SMSController smsController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        smsController = new SMSController(smsService, userService, webSocket);
    }

    @Test
    void smsSubmit_ValidPhoneNumber_ReturnsSuccessResponse() {
        // Arrange
        String phoneNumber = "9443594779";
        User mockUser = new User();
        when(userService.getUserViaPhoneNumber(phoneNumber)).thenReturn(mockUser);

        // Act
        ResponseEntity<ApiResponse> response = smsController.smsSubmit(phoneNumber);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("OTP sent successfully", response.getBody().getMessage());
        verify(webSocket).convertAndSend(anyString(), anyString());
        verify(smsService).send(phoneNumber);
    }

    @Test
    void verifyOTP_ValidData_ReturnsSuccessResponse() throws OtpValidationException, IOException, OtpValidationException {
        // Arrange
        String phoneNumber = "9443594779";
        int otp = 1234;

        // Act
        ResponseEntity<ApiResponse> response = smsController.verifyOTP(httpresponse, request, phoneNumber, otp);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("OTP is verified", response.getBody().getMessage());
        verify(smsService).verifyOtp(phoneNumber, otp, httpresponse, request);
    }
}
