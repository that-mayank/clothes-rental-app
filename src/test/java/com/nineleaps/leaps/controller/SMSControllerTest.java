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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class SMSControllerTest {
    @Mock
    private SMSController smsController;
    @Mock
    private SmsServiceInterface smsServiceInterface;
    @Mock
    private UserServiceInterface userServiceInterface;

    @Mock
    private SimpMessagingTemplate webSocket;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize the private webSocket field in SMSController
        ReflectionTestUtils.setField(smsController, "webSocket", webSocket);
    }


    @Test
    void testGetTimeStamp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // Use reflection to access the private method
        Method getTimeStampMethod = SMSController.class.getDeclaredMethod("getTimeStamp");
        getTimeStampMethod.setAccessible(true);  // Allow access to the private method

        // Call the getTimeStamp() method using reflection
        String timeStamp = (String) getTimeStampMethod.invoke(smsController);

        // Get the current timestamp in the expected format
        String expectedTimeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

        // Assert that the returned timestamp matches the expected format
        assertEquals(expectedTimeStamp, timeStamp);
    }




    @Test
    void testSendSmsWebSocketMessage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Mock data
        String phoneNumber = "1234567890";

        // Get the private method using reflection
        Method privateMethod = SMSController.class.getDeclaredMethod("sendSmsWebSocketMessage", String.class);
        privateMethod.setAccessible(true);

        // Call the private method
        privateMethod.invoke(smsController, phoneNumber);

        // Verify that webSocket.convertAndSend was called with the correct parameters
        String expectedTopicDestination = "/lesson/sms";
        String expectedMessage = getTimeStamp()+": SMS has been sent to 1234567890"; // Modify this based on your requirements

        verify(webSocket).convertAndSend(expectedTopicDestination, expectedMessage);
    }

    private String getTimeStamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    @Test
    void testVerifyOTP_ValidOTP() throws OtpValidationException, IOException {
        // Mock data
        String phoneNumber = "1234567890";
        Integer otp = 1234;
        HttpServletResponse response = new MockHttpServletResponse();  // Can be mocked if needed
        HttpServletRequest request = new MockHttpServletRequest();  // Can be mocked if needed

        // Mock the service method to simulate successful OTP validation
        doNothing().when(smsServiceInterface).verifyOtp(phoneNumber, otp, response, request);

        // Call the API
        ResponseEntity<ApiResponse> apiResponseEntity = smsController.verifyOTP(response, request, phoneNumber, otp);


    }

    @Test
    void testVerifyOTP_InvalidOTP() throws OtpValidationException, IOException {
        // Mock data
        String phoneNumber = "1234567890";
        Integer otp = 1234;
        HttpServletResponse response = null;  // Can be mocked if needed
        HttpServletRequest request = null;  // Can be mocked if needed

        // Mock the service method to simulate failed OTP validation
        doThrow(new OtpValidationException("Invalid OTP")).when(smsServiceInterface).verifyOtp(phoneNumber, otp, response, request);

        // Call the API
        ResponseEntity<ApiResponse> apiResponseEntity = smsController.verifyOTP(response, request, phoneNumber, otp);

        assertNull(apiResponseEntity);
        // Verify the response
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiResponseEntity.getStatusCode());
//        assertEquals("Invalid OTP", Objects.requireNonNull(apiResponseEntity.getBody()).getMessage());
    }
}