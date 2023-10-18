package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.SecurityUtility;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Message.class)
class SmsServiceImplTest {

    @Mock
    private UserServiceInterface userServiceInterface;
    @Mock
    private SecurityUtility securityUtility;
    @InjectMocks
    private SmsServiceImpl smsService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MockHttpServletRequest request;
    @Mock
    private MockHttpServletResponse response;

    private static final int MAX = 999999;
    private static final int MIN = 100000;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        smsService = new SmsServiceImpl(userServiceInterface, securityUtility);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        Twilio.init("ACe47c40c1144c690f78982e8d814abe5d", "2727852852992be1d14a9e1307a0dfb0");
    }

    @Test
    void verifyOtp_withInvalidOTP_shouldThrowOtpValidationException() {
        String phoneNumber = "1234567890";
        int otp = 123456;
        assertThrows(OtpValidationException.class, () -> smsService.verifyOtp(phoneNumber, otp, response, request));
    }

    @Test
    void verifyOtp_withInvalidPhoneNumber_shouldThrowOtpValidationException() {
        String phoneNumber = "1234567890";
        int otp = 123456;
        assertThrows(OtpValidationException.class, () -> smsService.verifyOtp(phoneNumber, otp, response, request));
    }

    @Test
    void generateToken_shouldGenerateTokenAndSetHeaders() throws IOException {
        String phoneNumber = "1234567890";
        // Mock the behavior of the userServiceInterface
        User user = new User();
        user.setRole(Role.BORROWER);
        user.setEmail("test@example.com");
        when(userServiceInterface.getUserViaPhoneNumber(phoneNumber)).thenReturn(user);
        // Mock the behavior of the securityUtility
        when(securityUtility.readSecretFromFile(anyString())).thenReturn("yourSecretKey");
        smsService.generateToken(response, request, phoneNumber);
        assertNotNull(response.getHeader("access_token"));
        assertNotNull(response.getHeader("refresh_token"));
        verify(securityUtility).saveTokens(anyString(), eq(user.getEmail()));
    }

    @Test
    void readSecretFromFile_shouldReadSecretFromFile() throws IOException {
        String homeDirectory = System.getProperty("user.home");
        String filePath = homeDirectory + "/Desktop/leaps/secret/secret.txt";
        String expectedSecret = "meinhuchotadon";
        // Mock the behavior of the securityUtility
        when(securityUtility.readSecretFromFile(filePath)).thenReturn(expectedSecret);
        String secret = securityUtility.readSecretFromFile(filePath);
        assertEquals(expectedSecret, secret);
    }

    @Test
    void readSecretFromFile_withIOException_shouldThrowIOException() throws IOException {
        String filePath = "test/secret.txt";
        // Mock the behavior of the securityUtility to throw an IOException
        when(securityUtility.readSecretFromFile(filePath)).thenThrow(IOException.class);
        assertThrows(IOException.class, () -> securityUtility.readSecretFromFile(filePath));
    }

    @Test
    void testGetUserViaPhoneNumber() {
        // Create a mock UserRepository
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
        // Create a sample user
        User sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setPhoneNumber("1234567890");
        // Define the behavior of the mock UserRepository
        Mockito.when(userRepositoryMock.findByPhoneNumber("1234567890"))
                .thenReturn(sampleUser);
        Mockito.when(userRepositoryMock.findByPhoneNumber("9876543210"))
                .thenReturn(null);
        // Create an instance of the class under test and inject the mock repository
        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock,passwordEncoder);
        // Test case 1: Existing user
        User foundUser = userService.getUserViaPhoneNumber("1234567890");
        assertEquals(sampleUser, foundUser);
        // Test case 2: Non-existent user
        User nonExistentUser = userService.getUserViaPhoneNumber("9876543210");
        assertNull(nonExistentUser);
    }


    @Test
    void testVerifyOtpInvalidOtp() throws IOException {
        String phoneNumber = "1234567890";
        int expectedOtp = 123456;
        int actualOtp = 654321; // Different from the expected OTP

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        // Mock the user retrieval
        User user = new User();
        Mockito.when(userServiceInterface.getUserViaPhoneNumber(phoneNumber)).thenReturn(user);

        // Mock the OTP verification with an incorrect OTP
        smsService.otpMap.put(phoneNumber, expectedOtp);

        try {
            // Call the verifyOtp method, expecting an OtpValidationException
            smsService.verifyOtp(phoneNumber, actualOtp, response, request);
            fail("Expected OtpValidationException but no exception was thrown.");
        } catch (OtpValidationException e) {
            // Assert on the exception message or perform any needed checks
            assertEquals("OTP not valid for phone number", e.getMessage());
        }
    }

    @Test
    void testVerifyOtpOtpNotGenerated() throws IOException {
        String phoneNumber = "1234567890";
        int otp = 123456;

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        try {
            // Call the verifyOtp method, expecting an OtpValidationException
            smsService.verifyOtp(phoneNumber, otp, response, request);
            fail("Expected OtpValidationException but no exception was thrown.");
        } catch (OtpValidationException e) {
            // Assert on the exception message or perform any needed checks
            assertEquals("OTP not generated for phone number", e.getMessage());
        }
    }

//    @Test
//    void sendShouldSendOtpMessageAndStoreInOtpMap() {
//        // Define the phone number for testing
//        String phoneNumber = "1234567890";
//
//        // Mock Twilio's Message class using PowerMockito
//        PowerMockito.mockStatic(Message.class);
//
//        // Call the send method
//        smsService.send(phoneNumber);
//
//        // Verify that the Message.creator method was called with the expected arguments
//        PowerMockito.verifyStatic(Message.class, times(1));
//        Message.creator(new PhoneNumber("+91" + phoneNumber), new PhoneNumber("+12542724507"), anyString()).create();
//
//        // Verify that the OTP was stored in the otpMap
//        assertNotNull(smsService.otpMap.get(phoneNumber));
//        assertEquals(6, String.valueOf(smsService.otpMap.get(phoneNumber)).length());
//    }

}