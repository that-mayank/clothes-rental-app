package com.nineleaps.leaps.service.implementation;
import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.SecurityUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.twilio.Twilio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


class SmsServiceImplTest {
    @Mock
    private UserServiceImpl userService;
    @Mock
    private UserServiceInterface userServiceInterface;
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityUtility securityUtility;

    @InjectMocks
    private SmsServiceImpl smsService;

    @Value("${twilio.account_sid}")
    private String accountSid;
    @Value("${twilio.token}")
    private String authToken;
    @Value("${twilio.from_number}")
    private String fromNumber;

    @BeforeAll
    static void setUpTwilio() {
        String accountSid = "ACe47c40c1144c690f78982e8d814abe5d";
        String authToken = "2727852852992be1d14a9e1307a0dfb0";
        Twilio.init(accountSid, authToken);

    }


    private Map<String, Integer> otpMap;

    @BeforeEach
    public void setup() {
        smsService = new SmsServiceImpl(userService, securityUtility);
        otpMap = new HashMap<>();
        //setPrivateField(smsService, "otpMap", otpMap);
    }


    @Test
    public void testVerifyOtpInvalid() throws IOException {
        String phoneNumber = "1234567890";
        Integer otp = 123456;
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        otpMap.put(phoneNumber, otp);

        Assertions.assertThrows(OtpValidationException.class, () -> smsService.verifyOtp(phoneNumber, 654321, response, request));
        Assertions.assertTrue(otpMap.containsKey(phoneNumber));
    }

    @Test
    public void testVerifyOtpNotGenerated() throws IOException {
        String phoneNumber = "1234567890";
        Integer otp = 123456;
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Assertions.assertThrows(OtpValidationException.class, () -> smsService.verifyOtp(phoneNumber, otp, response, request));
        Assertions.assertFalse(otpMap.containsKey(phoneNumber));
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
        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, passwordEncoder);

        // Test case 1: Existing user
        User foundUser = userService.getUserViaPhoneNumber("1234567890");
        assertEquals(sampleUser, foundUser);

        // Test case 2: Non-existent user
        User nonExistentUser = userService.getUserViaPhoneNumber("9876543210");
        assertNull(nonExistentUser);
    }

}