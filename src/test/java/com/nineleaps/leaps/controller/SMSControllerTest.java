package com.nineleaps.leaps.controller;


import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.exceptions.InvalidOtpException;
import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.SecurityUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SMSControllerTest {
    @InjectMocks
    private SMSController smsController;
    @Mock
    private SmsServiceInterface smsServiceInterface;
    @Mock
    private SecurityUtility securityUtility;

    @Mock
    private  HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

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
     void testVerifyOTP() throws OtpValidationException, IOException {
        String phoneNumber = "1234567890";
        int otp = 1234;

        // Stub the verifyOtp method to return true (OTP verification successful)
        when(smsServiceInterface.verifyOtp(eq(phoneNumber), eq(otp))).thenReturn(true);

        // Mock the generateToken method to do nothing
        doNothing().when(securityUtility).generateToken(any(HttpServletResponse.class), any(HttpServletRequest.class), eq(phoneNumber));

        // Call the verifyOTP method
        ResponseEntity<ApiResponse> apiResponseEntity = smsController.verifyOTP(response, request, phoneNumber, otp);

        // Verify the response
        assertEquals(HttpStatus.OK, apiResponseEntity.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponseEntity.getBody()).isSuccess());
        assertEquals("OTP is verified", apiResponseEntity.getBody().getMessage());
    }

    @Test
     void testSendSms() throws InvalidOtpException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String phoneNumber = "1234567890";

        // Stub the send method to do nothing (as it's void)
        doNothing().when(smsServiceInterface).send(eq(phoneNumber));
        // Get the private method using reflection
        Method privateMethod = SMSController.class.getDeclaredMethod("sendSms", String.class);
        privateMethod.setAccessible(true);

        // Call the private method
        privateMethod.invoke(smsController, phoneNumber);



        // Verify that smsService.send was called with the correct argument
        verify(smsServiceInterface).send(eq(phoneNumber));
    }

    @Test
     void testIsPhoneNumberPresent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String existingPhoneNumber = "1234567890";
        String nonExistingPhoneNumber = "9876543210";
        User user = new User();
        user.setId(1L);
        user.setPhoneNumber(existingPhoneNumber);
        // Get the private method using reflection
        Method privateMethod = SMSController.class.getDeclaredMethod("isPhoneNumberPresent", String.class);
        privateMethod.setAccessible(true);

        // Call the private method
        boolean result = (boolean) privateMethod.invoke(smsController, existingPhoneNumber);
        boolean result2 = (boolean) privateMethod.invoke(smsController, nonExistingPhoneNumber);

        // Stub the userService.getUserViaPhoneNumber method
        when(userServiceInterface.getUserViaPhoneNumber(existingPhoneNumber)).thenReturn(user);
        when(userServiceInterface.getUserViaPhoneNumber(nonExistingPhoneNumber)).thenReturn(null);

        assertFalse(result);
        assertFalse(result2);

    }

    @Test
     void testSmsSubmit() throws InvalidOtpException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String existingPhoneNumber = "1234567890";
        String nonExistingPhoneNumber = "9876543210";
        User user = new User();
        user.setId(1L);
        user.setPhoneNumber(existingPhoneNumber);
        // Get the private method using reflection
        Method privateMethod = SMSController.class.getDeclaredMethod("isPhoneNumberPresent", String.class);
        privateMethod.setAccessible(true);

        // Call the private method
        boolean result = (boolean) privateMethod.invoke(smsController, existingPhoneNumber);
        boolean result2 = (boolean) privateMethod.invoke(smsController, nonExistingPhoneNumber);
        // Stub the userService.getUserViaPhoneNumber method
        when(userServiceInterface.getUserViaPhoneNumber(existingPhoneNumber)).thenReturn(user);

        // Stub the sendSms method to do nothing (as it's void)
        doNothing().when(smsServiceInterface).send(eq(existingPhoneNumber));


        // Call the smsSubmit method
        ApiResponse response1 = smsController.smsSubmit(existingPhoneNumber).getBody();
        ApiResponse response2 = smsController.smsSubmit(nonExistingPhoneNumber).getBody();



        assertFalse(response2.isSuccess());
        assertEquals("Phone number not present in the database", response2.getMessage());


    }
}