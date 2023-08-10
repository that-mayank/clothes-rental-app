package com.nineleaps.leaps.service.implementation;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.DeviceTokenRepository;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.SecurityUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SmsServiceImplTest {

    @Mock
    private UserServiceInterface userServiceInterface;

    @Mock
    private SecurityUtility securityUtility;

    private SmsServiceImpl smsService;
    @Mock
    private PasswordEncoder passwordEncoder;

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

        String secret = securityUtility.readSecretFromFile(filePath);

        assertEquals(expectedSecret, secret);
    }

    @Test
    void readSecretFromFile_withIOException_shouldThrowIOException() throws IOException {
        String filePath = "test/secret.txt";

        assertThrows(IOException.class, () -> securityUtility.readSecretFromFile(filePath));
    }
    @Test
    public void testGetUserViaPhoneNumber() {
        // Create a mock UserRepository
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
        DeviceTokenRepository deviceTokenRepositoryMock = Mockito.mock(DeviceTokenRepository.class);

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
        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock,passwordEncoder,deviceTokenRepositoryMock);

        // Test case 1: Existing user
        User foundUser = userService.getUserViaPhoneNumber("1234567890");
        assertEquals(sampleUser, foundUser);

        // Test case 2: Non-existent user
        User nonExistentUser = userService.getUserViaPhoneNumber("9876543210");
        assertNull(nonExistentUser);
    }
}
