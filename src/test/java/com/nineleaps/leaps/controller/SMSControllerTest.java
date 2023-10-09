package com.nineleaps.leaps.controller;


import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.exceptions.InvalidOtpException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.SecurityUtility;
import org.junit.jupiter.api.*;
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
@Tag("unit_tests")
@DisplayName("test case file for SMS Controller")
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
    @DisplayName("get time stamp")
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
    @DisplayName("Send Sms")
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
    @DisplayName("Verify OTP")
     void testVerifyOTP() throws  IOException {
        String phoneNumber = "1234567890";
        int otp = 1234;

        // Stub the verifyOtp method to return true (OTP verification successful)
        when(smsServiceInterface.verifyOtp(phoneNumber, otp)).thenReturn(true);

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
    @DisplayName("Send SMS")
     void testSendSms() throws InvalidOtpException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String phoneNumber = "1234567890";

        // Stub the send method to do nothing (as it's void)
        doNothing().when(smsServiceInterface).send(phoneNumber);
        // Get the private method using reflection
        Method privateMethod = SMSController.class.getDeclaredMethod("sendSms", String.class);
        privateMethod.setAccessible(true);

        // Call the private method
        privateMethod.invoke(smsController, phoneNumber);



        // Verify that smsService.send was called with the correct argument
        verify(smsServiceInterface).send(phoneNumber);
    }

    @Test
    @DisplayName("Check if phone number is present")
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
    @DisplayName("SMS Submit")
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
        doNothing().when(smsServiceInterface).send(existingPhoneNumber);


        // Call the smsSubmit method
        ApiResponse response1 = smsController.smsSubmit(existingPhoneNumber).getBody();
        ApiResponse response2 = smsController.smsSubmit(nonExistingPhoneNumber).getBody();



        assertFalse(response2.isSuccess());
        assertEquals("Phone number not present in the database", response2.getMessage());


    }

    @Test
    @DisplayName("Invalid OTP")
     void testSmsSubmitInvalidOtpException() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String phoneNumber = "1234567890";

        // Mock isPhoneNumberPresent to return true (assuming the phone number is present)
        when(userServiceInterface.getUserViaPhoneNumber(phoneNumber)).thenReturn(new User());

        // Mock sendSms to throw InvalidOtpException
        doThrow(new InvalidOtpException("Invalid OTP")).when(smsServiceInterface).send(phoneNumber);

        // Access private method isPhoneNumberPresent using reflection
        Method isPhoneNumberPresentMethod = SMSController.class.getDeclaredMethod("isPhoneNumberPresent", String.class);
        isPhoneNumberPresentMethod.setAccessible(true);

        // Access private method sendSms using reflection
        Method sendSmsMethod = SMSController.class.getDeclaredMethod("sendSms", String.class);
        sendSmsMethod.setAccessible(true);

        // Call the smsSubmit method
        ResponseEntity<ApiResponse> responseEntity = smsController.smsSubmit(phoneNumber);

        // Verify the response and status
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Invalid OTP", Objects.requireNonNull(responseEntity.getBody()).getMessage());
        Assertions.assertFalse(responseEntity.getBody().isSuccess());
    }

    // Helper method to set a private field in a class using reflection
    private void setPrivateField(Object targetObject, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, value);
    }

    @Test
    @DisplayName("Verify OTP - 2")
     void testVerifyOTP2() throws IOException {
        String phoneNumber = "1234567890";
        int otp = 1234;

        // Mock smsService.verifyOtp to return true
        when(smsServiceInterface.verifyOtp(phoneNumber, otp)).thenReturn(true);

        // Call the verifyOTP method
        ResponseEntity<ApiResponse> responseEntity = smsController.verifyOTP(null, null, phoneNumber, otp);

        // Verify that securityUtility.generateToken is called
        verify(securityUtility, times(1)).generateToken(null, null, phoneNumber);

        // Verify the response and status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(Objects.requireNonNull(responseEntity.getBody()).isSuccess());
        assertEquals("OTP is verified", responseEntity.getBody().getMessage());
    }
}