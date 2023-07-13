package com.nineleaps.leaps.service.implementation;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.SecurityUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SmsServiceImplTest {

    @Mock
    private UserServiceInterface userServiceInterface;

    @Mock
    private SecurityUtility securityUtility;

    private SmsServiceImpl smsService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        smsService = new SmsServiceImpl(userServiceInterface, securityUtility);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
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

        String secret = smsService.readSecretFromFile(filePath);

        assertEquals(expectedSecret, secret);
    }

    @Test
    void readSecretFromFile_withIOException_shouldThrowIOException() throws IOException {
        String filePath = "test/secret.txt";

        assertThrows(IOException.class, () -> smsService.readSecretFromFile(filePath));
    }
}
