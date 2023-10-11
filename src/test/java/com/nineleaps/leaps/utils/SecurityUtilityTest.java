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

import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.time.LocalDateTime;

import java.time.ZoneId;

import java.util.Arrays;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class SecurityUtilityTest {

    @InjectMocks

    private SecurityUtility securityUtility;

    @Mock

    private UserServiceInterface userServiceInterface;

    @Mock

    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach

    void setUp() {

        MockitoAnnotations.openMocks(this);

        securityUtility = new SecurityUtility(userServiceInterface, refreshTokenRepository);

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

    void isAccessTokenExpired_ExpiredToken_ReturnsTrue() {

        // Arrange

        String accessToken = generateAccessToken(-60); // Generate an expired access token (60 minutes ago)

        // Act

        boolean isExpired = securityUtility.isAccessTokenExpired(accessToken);

        // Assert

        assertTrue(isExpired);

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

        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));

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

                .withIssuer("http://example.com")

                .sign(algorithm);

    }

}