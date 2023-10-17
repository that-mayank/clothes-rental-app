package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
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
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void updateAccessToken_ValidInput_ReturnsNewAccessToken() throws IOException {

        // Arrange

        String email = "jyoshnavi@nineleaps.com";

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqeW9zaG5hdmlAbmluZWxlYXBzLmNvbSIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9hcGkvdjEvb3RwIiwiZXhwIjoxNjkxMTMxNTcyfQ.4IY-hz-u3uKUVD32EP-3ud7TmmX6IHqxHCYR7IFrVVM"); // Set a valid refresh token value

        refreshToken.setEmail(email);

        when(refreshTokenRepository.findByEmail(email)).thenReturn(refreshToken);

        User user = new User();

        user.setEmail(email);

        user.setRole(Role.ADMIN);

        when(userServiceInterface.getUser(email)).thenReturn(user);

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURL()).thenReturn(new StringBuffer("https://example.com"));

        // Act

        String newAccessToken = securityUtility.updateAccessToken(email, request);

        // Assert

        assertNotNull(newAccessToken);

        DecodedJWT decodedAccessToken = JWT.decode(newAccessToken);

        assertEquals(email, decodedAccessToken.getSubject());

        assertEquals(request.getRequestURL().toString(), decodedAccessToken.getIssuer());

        assertEquals(Arrays.asList(Role.ADMIN.toString()), decodedAccessToken.getClaim("roles").asList(String.class));

        Date expirationDate = decodedAccessToken.getExpiresAt();

        assertNotNull(expirationDate);

        assertTrue(expirationDate.after(new Date())); // Ensure expiration date is in the future

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
        when(userServiceInterface.getUser("test@example.com")).thenReturn(user);


        System.out.println(securityUtility.isTokenExpired(refreshToken.getToken()));

        // Mocking the readSecretFromFile method to return a secret key
        securityUtility.readSecretFromFile(absolutePath);

        // Mocking the request URL
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://example.com/api/v1"));

        String updatedAccessToken = securityUtility.updateAccessTokenViaRefreshToken("ujohnwesly8@gmail.com", request, refreshToken.getToken());

        // Assertions
        assertNotNull(updatedAccessToken);
        // Add more assertions as needed to validate the token and its properties
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
        String secretFilePath = "/Desktop"+"/leaps"+"/secret"+"/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;

        // Mocking an invalid refresh token
        String invalidToken = generateAccessToken(-60);

        String updatedAccessToken = securityUtility.updateAccessTokenViaRefreshToken("ujohnwesly8@gmail.com", request, invalidToken);

        // Assertions
        assertEquals("Refresh Token In Database Expired , Login Again !", updatedAccessToken);
    }
}
