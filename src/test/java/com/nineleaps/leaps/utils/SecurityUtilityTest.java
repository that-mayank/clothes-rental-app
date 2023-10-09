package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserLoginInfo;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.repository.UserLoginInfoRepository;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.LockedException;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.nineleaps.leaps.LeapsApplication.MAX_LOGIN_ATTEMPTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilityTest {
    private SecurityUtility securityUtility;

    @Mock
    private UserServiceInterface userServiceInterface;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;


    @Mock
    private  UserLoginInfoRepository userLoginInfoRepository;
    @Mock
    private  UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize and mock userRepository
        userRepository = mock(UserRepository.class);
        securityUtility = new SecurityUtility(userServiceInterface, refreshTokenRepository,userLoginInfoRepository,userRepository);
    }

    @Test
    void isAccessTokenExpired_ValidToken_ReturnsFalse() {
        // Arrange
        String accessToken = generateAccessToken(60); // Generate an access token with 60 minutes expiration time

        // Act
        boolean isExpired = securityUtility.isAccessTokenExpired(accessToken);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void isRefreshTokenExpired_ValidToken_ReturnsFalse() {
        // Arrange
        String refreshToken = generateRefreshToken(60); // Generate an access token with 60 minutes expiration time

        // Act
        boolean isExpired = securityUtility.isAccessTokenExpired(refreshToken);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void isAccessTokenExpired_ExpiredToken_ReturnsTrue() {
        // Arrange
        String accessToken = generateAccessToken(-60); // Generate an expired access token (60 minutes ago)

        // Act
        boolean isExpired = securityUtility.isRefreshTokenExpired(accessToken);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void isRefreshTokenExpired_ExpiredToken_ReturnsTrue() {
        // Arrange
        String refreshToken = generateRefreshToken(-60); // Generate an expired access token (60 minutes ago)

        // Act
        boolean isExpired = securityUtility.isRefreshTokenExpired(refreshToken);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void saveTokens_ValidInput_ReturnsTrue() {
        // Arrange
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";
        LocalDateTime dateTime = LocalDateTime.now();

        // Act
        boolean result = securityUtility.saveTokens(refreshToken, email,dateTime);

        // Assert
        assertTrue(result);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void updateAccessToken_ValidInput_ReturnsNewAccessToken() throws IOException {
        // Create a valid token
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1am9obndlc2x5OEBnbWFpbC5jb20iLCJyb2xlcyI6WyJPV05FUiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL3YxL2xvZ2luIiwiZXhwIjoxNzAxODE4Mjc5fQ.XuDMsvq6290oyS4hN5aNda879Gy2yoJzWCmJHEGn_Bs";
        // Arrange
        String email = "ujohnwesly8@gmail.com";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token); // Set a valid refresh token value
        refreshToken.setEmail(email);

        when(refreshTokenRepository.findByEmail(email)).thenReturn(refreshToken);

        User user = new User();
        user.setEmail(email);
        user.setRole(Role.OWNER);
        when(userServiceInterface.getUser(email)).thenReturn(user);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://example.com"));


        // Act
        String newAccessToken = securityUtility.updateAccessTokenViaRefreshToken(email, request,token);

        // Assert
        assertNotNull(newAccessToken);
        DecodedJWT decodedAccessToken = JWT.decode(newAccessToken);
        assertEquals(email, decodedAccessToken.getSubject());
        assertEquals(request.getRequestURL().toString(), decodedAccessToken.getIssuer());
        assertEquals(Collections.singletonList(Role.OWNER.toString()), decodedAccessToken.getClaim("roles").asList(String.class));
        Date expirationDate = decodedAccessToken.getExpiresAt();
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date())); // Ensure expiration date is in the future
    }

    @Test
    void testReadSecretFromFile() throws IOException {
        // Arrange
        String secret = "meinhuchotadon";
        String secretFilePath = "/Desktop/leaps/secret/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;

        // Mock the behavior of FileReaderWrapper
        BufferedReader bufferedReader = mock(BufferedReader.class);
        when(bufferedReader.readLine()).thenReturn(secret); // Return the secret when readLine is called


        // Act
        String readSecret = securityUtility.readSecretFromFile(absolutePath);

        // Assert
        assertEquals(secret, readSecret);
    }


    private String generateAccessToken(int expirationMinutes) {
        Algorithm algorithm = Algorithm.HMAC256("secret"); // Replace "secret" with your actual secret key
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(expirationMinutes);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
        return JWT.create()
                .withSubject("test@example.com")
                .withExpiresAt(expirationDate)
                .withIssuer("https://example.com")
                .sign(algorithm);
    }

    private String generateRefreshToken(int expirationMinutes) {
        Algorithm algorithm = Algorithm.HMAC256("secret"); // Replace "secret" with your actual secret key
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(expirationMinutes);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
        return JWT.create()
                .withSubject("test@example.com")
                .withExpiresAt(expirationDate)
                .withIssuer("https://example.com")
                .sign(algorithm);
    }


    @Test
    void testGetDeviceToken_ExistingDeviceToken() {
        // Arrange
        String email = "ujohnwesly8@gmail.com";
        String deviceToken = "existingDeviceToken";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setDeviceToken("existingDeviceToken");

        when(userRepository.findByEmail(email)).thenReturn(user);

        // Act
        securityUtility.getDeviceToken(email, deviceToken);



        assertEquals(deviceToken, user.getDeviceToken());
        // Add more assertions as needed to achieve 100% coverage
    }

    @Test
    void testGetDeviceToken_NoExistingDeviceToken() {
        // Arrange
        String email = "test@example.com";
        String deviceToken = "testDeviceToken";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        // Act
        securityUtility.getDeviceToken(email, deviceToken);


        assertNull(user.getDeviceToken());
        // Add more assertions as needed to achieve 100% coverage
    }

    @Test
    void testUpdateLoginAttempts() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);
        UserLoginInfoRepository userLoginInfoRepository = mock(UserLoginInfoRepository.class);

        SecurityUtility securityUtility1 = new SecurityUtility(userServiceInterface,refreshTokenRepository,userLoginInfoRepository,userRepository);

        // Create a user and login info
        User user = new User();
        user.setId(1L);  // Set a user ID

        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setUser(user);
        loginInfo.setLoginAttempts(0);  // Set initial login attempts

        // Mock repository behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userLoginInfoRepository.findByUserId(user.getId())).thenReturn(loginInfo);

        // Call the method to be tested
        securityUtility1.updateLoginAttempts("test@example.com");

        // Verify login attempts and locked status
        assertEquals(1, loginInfo.getLoginAttempts());

        // ArgumentCaptor to capture the saved login info
        ArgumentCaptor<UserLoginInfo> loginInfoCaptor = ArgumentCaptor.forClass(UserLoginInfo.class);
        verify(userLoginInfoRepository).save(loginInfoCaptor.capture());

        // Check the saved login info
        UserLoginInfo savedLoginInfo = loginInfoCaptor.getValue();
        assertEquals(loginInfo.getLoginAttempts(), savedLoginInfo.getLoginAttempts());
        assertEquals(loginInfo.isAccountLocked(), savedLoginInfo.isAccountLocked());
        // Add more assertions based on your logic
    }

    @Test
    void testCheckAccountLockAndLoginAttempts() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);
        UserLoginInfoRepository userLoginInfoRepository = mock(UserLoginInfoRepository.class);

        // Create an instance of the class you're testing
        SecurityUtility securityUtility1 = new SecurityUtility(userServiceInterface,refreshTokenRepository,userLoginInfoRepository,userRepository);

        // Create a user and login info
        User user = new User();
        user.setId(1L);  // Set a user ID

        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setUser(user);
        loginInfo.setLocked(true);
        loginInfo.setLockTime(LocalDateTime.now().plusHours(1));  // Set a lock time 1 hour in the future

        // Mock repository behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userLoginInfoRepository.findByUserId(user.getId())).thenReturn(loginInfo);

        // Call the method to be tested
        assertThrows(LockedException.class, () -> securityUtility1.checkAccountLockAndLoginAttempts("test@example.com"));

        // Verify repository interactions
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userLoginInfoRepository, times(1)).findByUserId(user.getId());
    }


    @Test
    void testSetLastLoginAttempt() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);
        UserLoginInfoRepository userLoginInfoRepository = mock(UserLoginInfoRepository.class);

        // Create an instance of the class you're testing
        SecurityUtility securityUtility1 = new SecurityUtility(userServiceInterface,refreshTokenRepository,userLoginInfoRepository,userRepository);

        // Create a user and login info
        User user = new User();
        user.setId(1L);  // Set a user ID

        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setUser(user);

        // Mock repository behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userLoginInfoRepository.findByUserId(user.getId())).thenReturn(loginInfo);

        // Call the method to be tested
        securityUtility1.setLastLoginAttempt("test@example.com");

        // Verify that the userLoginInfo's last login attempt was set and saved
        verify(userLoginInfoRepository, times(1)).save(loginInfo);
        assertNotNull(loginInfo.getLastLoginAttempt());
    }

    @Test
    void testInitializeUserLoginInfo() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);
        UserLoginInfoRepository userLoginInfoRepository = mock(UserLoginInfoRepository.class);

        // Create an instance of the class you're testing
        SecurityUtility securityUtility1 = new SecurityUtility(userServiceInterface,refreshTokenRepository,userLoginInfoRepository,userRepository);

        // Create a user and login info
        User user = new User();
        user.setId(1L);  // Set a user ID

        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setUser(user);

        // Mock repository behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userLoginInfoRepository.findByUserId(user.getId())).thenReturn(loginInfo);

        // Call the method to be tested
        securityUtility1.initializeUserLoginInfo("test@example.com");


        assertFalse(loginInfo.isAccountLocked());
    }

    @Test
    void updateLoginAttempts_ExceedMaxLoginAttempts_LockAccount() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);  // Set a valid user ID
        when(userRepository.findByEmail(email)).thenReturn(user);

        Long userId = user.getId();

        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setUser(user);
        loginInfo.setLoginAttempts(MAX_LOGIN_ATTEMPTS + 1);  // Exceed max login attempts

        when(userLoginInfoRepository.findByUserId(userId)).thenReturn(loginInfo);

        // Act
        securityUtility.updateLoginAttempts(email);

        // Assert
        assertTrue(loginInfo.isAccountLocked());
        assertNotNull(loginInfo.getLockTime());
    }

    @Test
    void updateLoginAttempts_LoginInfoIsNull_InitializeAndSetLoginAttempts() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);  // Set a valid user ID
        when(userRepository.findByEmail(email)).thenReturn(user);

        Long userId = user.getId();

        // Simulate loginInfo being null
        when(userLoginInfoRepository.findByUserId(userId)).thenReturn(null);

        // Act
        securityUtility.updateLoginAttempts(email);

        // Verify that loginInfo was initialized and login attempts set to 1
        ArgumentCaptor<UserLoginInfo> loginInfoCaptor = ArgumentCaptor.forClass(UserLoginInfo.class);
        verify(userLoginInfoRepository).save(loginInfoCaptor.capture());

        UserLoginInfo savedLoginInfo = loginInfoCaptor.getValue();
        assertNotNull(savedLoginInfo);
        assertEquals(user, savedLoginInfo.getUser());
        assertEquals(1, savedLoginInfo.getLoginAttempts());
    }

    @Test
    void checkAccountLockAndLoginAttempts_UserLoginInfoIsNull_InitializeAndSaveLoginInfo() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);  // Set a valid user ID
        when(userRepository.findByEmail(email)).thenReturn(user);
        when(userLoginInfoRepository.findByUserId(user.getId())).thenReturn(null);

        // Act
        securityUtility.checkAccountLockAndLoginAttempts(email);

        // Verify that a new UserLoginInfo was initialized and saved
        ArgumentCaptor<UserLoginInfo> loginInfoCaptor = ArgumentCaptor.forClass(UserLoginInfo.class);
        verify(userLoginInfoRepository).save(loginInfoCaptor.capture());

        UserLoginInfo savedLoginInfo = loginInfoCaptor.getValue();
        assertNotNull(savedLoginInfo);
        assertEquals(user, savedLoginInfo.getUser());
    }

    @Test
    void checkAccountLockAndLoginAttempts_UserLoginInfoNotNull_CorrectLogicFollowed() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);  // Set a valid user ID
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setUser(user);
        userLoginInfo.setLockTime(LocalDateTime.now().plusMinutes(30));  // Set lock time 30 minutes from now
        userLoginInfo.setLocked(false);
        userLoginInfo.setLoginAttempts(1);
        when(userRepository.findByEmail(email)).thenReturn(user);
        when(userLoginInfoRepository.findByUserId(user.getId())).thenReturn(userLoginInfo);

        // Act
        securityUtility.checkAccountLockAndLoginAttempts(email);


        assertFalse(userLoginInfo.isAccountLocked());
        assertFalse(userLoginInfo.isAccountLocked());
        assertEquals(1, userLoginInfo.getLoginAttempts());
    }

    @Test
    void checkAccountLockAndLoginAttempts_AccountLocked_UnlockAccount() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);  // Set a valid user ID
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setUser(user);
        userLoginInfo.setLockTime(LocalDateTime.now().minusMinutes(30));  // Set lock time 30 minutes ago (account is locked)
        userLoginInfo.setLoginAttempts(4);
        userLoginInfo.setLocked(true);
        when(userRepository.findByEmail(email)).thenReturn(user);
        when(userLoginInfoRepository.findByUserId(user.getId())).thenReturn(userLoginInfo);

        // Act
        securityUtility.checkAccountLockAndLoginAttempts(email);


        assertFalse(userLoginInfo.isAccountLocked());
        assertTrue(LocalDateTime.now().isAfter(userLoginInfo.getLockTime()));
        assertEquals(4, userLoginInfo.getLoginAttempts());
    }


    @Test
    void testGenerateToken() throws IOException {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        SecurityUtility securityUtility1 = new SecurityUtility(userServiceInterface,refreshTokenRepository,userLoginInfoRepository,userRepository);

        // Create a user
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setRole(Role.OWNER);

        UserLoginInfo loginInfo = new UserLoginInfo();
        loginInfo.setUser(user);

        when(userLoginInfoRepository.findByUserId(user.getId())).thenReturn(loginInfo);

        // Mock repository behavior
        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(user);

        // Mock request behavior
        StringBuffer requestUrl = new StringBuffer("https://example.com");
        when(request.getRequestURL()).thenReturn(requestUrl);

        // Call the method to be tested
        securityUtility1.generateToken(response, request, "1234567890");

        // Verify that response headers are set
        ArgumentCaptor<String> headerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(response, times(2)).setHeader(headerNameCaptor.capture(), headerValueCaptor.capture());

        // Check the captured headers and values
        List<String> headerNames = headerNameCaptor.getAllValues();
        List<String> headerValues = headerValueCaptor.getAllValues();

        assertEquals("access_token", headerNames.get(0));
        assertNotNull(headerValues.get(0));

        assertEquals("refresh_token", headerNames.get(1));
        assertNotNull(headerValues.get(1));
    }

    @Test
    void updateAccessTokenViaRefreshToken_InvalidRefreshToken_ReturnsInvalidTokenMessage() throws IOException {
        // Arrange
        String email = "test@example.com";
        String invalidRefreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1am9obndlc2x5OEBnbWFpbC5jb20iLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL3YxL2xvZ2luIiwiZXhwIjoxNjk1ODM2OTc4fQ.kUq1AtOjJjs_V0fGyfKPJ_5Z_dDr0QCUvNeR6WfvmaU";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1am9obndlc2x5OEBnbWFpbC5jb20iLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL3YxL2xvZ2luIiwiZXhwIjoxNjk1ODM2OTc4fQ.kUq1AtOjJjs_V0fGyfKPJ_5Z_dDr0QCUvNeR6WfvmaU");
        when(refreshTokenRepository.findByEmail(email)).thenReturn(refreshToken);

        // Act
        String result = securityUtility.updateAccessTokenViaRefreshToken(email, mock(HttpServletRequest.class), invalidRefreshToken);

        // Assert
        assertEquals("Refresh Token In Database Expired , Login Again !", result);
    }

    @Test
    void updateAccessTokenViaRefreshToken_InvalidRefreshToken_ReturnsInvalidTokenMessageFromDb() throws IOException {
        // Arrange
        String email = "test@example.com";
        String invalidRefreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1am9obndlc2x5OEBnbWFpbC5jb20iLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL3YxL2xvZ2luIiwiZXhwIjoxNjk1ODM2OTc4fQ.kUq1AtOjJjs_V0fGyfKPJ_5Z_dDr0QCUvNeR6WfvmaU";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1am9obndlc2x5OEBnbWFpbC5jb20iLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL3YxL2xvZ2luIiwiZXhwIjoxNjk4NDI5MzE5fQ.9_yQ8BMH0aWaBcRyJd_iTM8b5IRMChNyXukptEjxokc");
        when(refreshTokenRepository.findByEmail(email)).thenReturn(refreshToken);

        // Act
        String result = securityUtility.updateAccessTokenViaRefreshToken(email, mock(HttpServletRequest.class), invalidRefreshToken);

        // Assert
        assertEquals("Invalid Refresh token", result);
    }
    @Test
    void testAccountLockAndLoginAttempts_AccountLockedBeforeUnlockTime() {
        // Mock data
        User user = new User();
        user.setId(1L);

        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setLocked(true);
        userLoginInfo.setLockTime(LocalDateTime.now().plusMinutes(10));

        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(userLoginInfoRepository.findByUserId(anyLong())).thenReturn(userLoginInfo);

        // Test the method
        assertThrows(LockedException.class, () -> securityUtility.checkAccountLockAndLoginAttempts("test@example.com"));
    }


}


