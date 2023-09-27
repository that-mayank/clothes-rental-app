package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.repository.UserLoginInfoRepository;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilityTest {
    private SecurityUtility securityUtility;

    @Mock
    private UserServiceInterface userServiceInterface;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;



    private  UserLoginInfoRepository userLoginInfoRepository;
    private  UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));


        // Act
        String newAccessToken = securityUtility.updateAccessTokenViaRefreshToken(email, request,token);

        // Assert
        assertNotNull(newAccessToken);
        DecodedJWT decodedAccessToken = JWT.decode(newAccessToken);
        assertEquals(email, decodedAccessToken.getSubject());
        assertEquals(request.getRequestURL().toString(), decodedAccessToken.getIssuer());
        assertEquals(Arrays.asList(Role.OWNER.toString()), decodedAccessToken.getClaim("roles").asList(String.class));
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
                .withIssuer("http://example.com")
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
                .withIssuer("http://example.com")
                .sign(algorithm);
    }



}