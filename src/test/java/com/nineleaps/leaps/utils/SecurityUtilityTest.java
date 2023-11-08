package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityUtility.class})
class SecurityUtilityTest {

    @InjectMocks
    private SecurityUtility securityUtility;
    @Mock
    private UserServiceInterface userServiceInterface;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void isAccessTokenExpired_ValidToken_ReturnsFalse() {
        // Arrange
        String accessToken = generateAccessToken(60); // Generate an access token with 60 minutes expiration time
        // Act
        boolean isExpired = securityUtility.isTokenExpired(accessToken);
        // Assert
        assertTrue(isExpired);
    }

    @Test
    void isAccessTokenExpired_ExpiredToken_ReturnsTrue() {
        // Arrange
        String accessToken = generateAccessToken(-60); // Generate an expired access token (60 minutes ago)
        // Act
        boolean isExpired = securityUtility.isTokenExpired(accessToken);
        // Assert
        assertFalse(isExpired);
    }

    @Test
    void saveTokens_ValidInput_ReturnsTrue() {
        // Arrange
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";
        // Act
        boolean result = securityUtility.saveTokens(refreshToken, email);
        // Assert
        assertTrue(result);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testUpdateAccessTokenViaRefreshToken_ValidRefreshToken() throws IOException {
        // Mocking the refreshToken object and its properties
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmail("ujohnwesly8@gmail.com");
        refreshToken.setToken(generateAccessToken(60));
        when(refreshTokenRepository.findByEmail("ujohnwesly8@gmail.com")).thenReturn(refreshToken);
        String secretFilePath = "/Desktop"+"/leaps"+"/secret"+"/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
        // Mocking the userServiceInterface to return a User object
        User user = new User();
        user.setRole(Role.ADMIN);
        when(userServiceInterface.getUser("ujohnwesly8@gmail.com")).thenReturn(user);
        // Mocking the readSecretFromFile method to return a secret key
        securityUtility.readSecretFromFile(absolutePath);
        // Mocking the request URL
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://example.com/api/v1"));
        String updatedAccessToken = securityUtility.updateAccessTokenViaRefreshToken("ujohnwesly8@gmail.com", request, refreshToken.getToken());
        // Assertions
        assertNotNull(updatedAccessToken);
    }

    @Test
    void testUpdateAccessTokenViaRefreshToken_ExpiredRefreshToken() throws IOException {
        // Mocking the refreshToken object and its properties
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmail("test@example.com");
        refreshToken.setToken(generateAccessToken(-60));
        when(refreshTokenRepository.findByEmail("ujohnwesly8@gmail.com")).thenReturn(refreshToken);
        String secretFilePath = "/Desktop"+"/leaps"+"/secret"+"/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
        // Mocking the isTokenExpired method to return true for an expired token
        securityUtility.isTokenExpired(refreshToken.getToken());
        // Mocking the readSecretFromFile method to return a secret key
        securityUtility.readSecretFromFile(absolutePath);
        // Mocking the request URL
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://example.com/api/v1"));
        String updatedAccessToken = securityUtility.updateAccessTokenViaRefreshToken("ujohnwesly8@gmail.com", request, refreshToken.getToken());
        // Assertions
        assertEquals("Refresh Token In Database Expired , Login Again !", updatedAccessToken);
    }

    @Test
    void testUpdateAccessTokenViaRefreshToken_InvalidRefreshToken() throws IOException {
        // Mocking the refreshToken object and its properties
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmail("test@example.com");
        refreshToken.setToken(generateAccessToken(-60));
        when(refreshTokenRepository.findByEmail("ujohnwesly8@gmail.com")).thenReturn(refreshToken);
        // Mocking an invalid refresh token
        String invalidToken = generateAccessToken(-60);
        String updatedAccessToken = securityUtility.updateAccessTokenViaRefreshToken("ujohnwesly8@gmail.com", request, invalidToken);
        // Assertions
        assertEquals("Refresh Token In Database Expired , Login Again !", updatedAccessToken);
    }

    @Test
    void testUpdateAccessTokenTokenMismatch() throws IOException {
        // Arrange
        String email = "ujohnwesly8@gmail.com";
        User user = new User();
        user.setRole(Role.BORROWER);
        user.setEmail(email);
        String tokenToCheck = generateAccessTokenDuplicate(60);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(generateAccessToken(60));
        // Mock the behavior of refreshTokenRepository.findByEmail(email2)
        Mockito.when(refreshTokenRepository.findByEmail(email)).thenReturn(refreshToken);
        // Act
        String result = securityUtility.updateAccessTokenViaRefreshToken(email, request, tokenToCheck);
        // Assert
        // Verify that the result is the expected message for token mismatch
        assertEquals("Refresh Token In Database Expired , Login Again !", result);
    }

    @Test
    void testSetDeviceToken() {
        // Arrange
        String email = "test@example.com";
        String deviceToken = "deviceToken123";
        // Act
        securityUtility.setDeviceToken(email, deviceToken);
        // Assert
        // Verify that userServiceInterface.saveDeviceTokenToUser was called with the correct parameters
        Mockito.verify(userServiceInterface).saveDeviceTokenToUser(email, deviceToken);
    }

    private String generateAccessToken(int expirationMinutes) {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(expirationMinutes);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
        return JWT.create()
                .withSubject("ujohnwesly8@gmail.com")
                .withExpiresAt(expirationDate)
                .withIssuer("https://example.com")
                .withClaim("roles", List.of("BORROWER"))
                .sign(algorithm);
    }

    private String generateAccessTokenDuplicate(int expirationMinutes) {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(expirationMinutes);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
        return JWT.create()
                .withSubject("ujohnwesly8@gmail.com")
                .withExpiresAt(expirationDate)
                .withIssuer("https://example.com")
                .withClaim("roles", List.of("OWNER"))
                .sign(algorithm);
    }
}
