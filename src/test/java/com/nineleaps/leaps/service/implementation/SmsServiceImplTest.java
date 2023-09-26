package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.utils.SecurityUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class SmsServiceImplTest {

    @Mock
    private SecurityUtility securityUtility;

    private SmsServiceImpl smsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        smsService = new SmsServiceImpl(securityUtility);
    }

//    @Test
//    void send() {
//        // Mocking Twilio API call, assuming Twilio API calls are successful
//        // We're not testing Twilio API here, so we just check if the method doesn't throw exceptions.
//        smsService.send("1234567890");
//    }
//
//    @Test
//    void verifyOtp_ValidOtp_Success() throws OtpValidationException, IOException {
//        // Setup
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        Integer otp = 123456;
//        String phoneNumber = "1234567890";
//        smsService.send(phoneNumber);
//
//        // Mock behavior
//        doNothing().when(securityUtility).generateToken(any(), any(), eq(phoneNumber));
//
//        // Test
//        smsService.verifyOtp(phoneNumber, otp, response, request);
//
//        // Verify
//        verify(securityUtility, times(1)).generateToken(any(), any(), eq(phoneNumber));
//    }
//
//    @Test
//    void verifyOtp_InvalidOtp_ThrowsOtpValidationException() {
//        // Setup
//        Integer invalidOtp = 999999;
//        String phoneNumber = "1234567890";
//        smsService.send(phoneNumber);
//
//        // Test and verify
//        try {
//            smsService.verifyOtp(phoneNumber, invalidOtp, new MockHttpServletResponse(), new MockHttpServletRequest());
//        } catch (OtpValidationException | IOException e) {
//            // Verify the exception message or any other assertions if needed
//            return;
//        }
//
//        // If no exception is thrown, fail the test
//        fail("Expected OtpValidationException but no exception was thrown.");
//    }

    // Add this test to set the test credentials
    @Test
    void setTestCredentials_ShouldSetTestCredentials() {
        // Use reflection to set the test credentials
        try {
            Field testAccountSidField = SmsServiceImpl.class.getDeclaredField("accountSid");
            testAccountSidField.setAccessible(true);
            testAccountSidField.set(smsService, "your_test_account_sid");

            Field testAuthTokenField = SmsServiceImpl.class.getDeclaredField("authToken");
            testAuthTokenField.setAccessible(true);
            testAuthTokenField.set(smsService, "your_test_auth_token");

            // Now you can proceed with your test using these test credentials
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set test credentials: " + e.getMessage());
        }
    }
}


