package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HelperTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private Helper helper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        helper = new Helper(userRepository);
    }

    @Test
    void notNull_withNonNullObject() {
        Object obj = new Object();
        assertTrue(Helper.notNull(obj));
    }

    @Test
    void notNull_withNullObject() {
        Object obj = null;
        assertFalse(Helper.notNull(obj));
    }

//    @Test
//    void testGetUser() {
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("test@example.com");
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        when(request.getHeader("Authorization")).thenReturn("Bearer Token"); // Replace with your actual access token
//
//        String email = "test@example.com";
//        DecodedJWT decodedJWT = mock(DecodedJWT.class);
//        when(decodedJWT.getSubject()).thenReturn(email);
//        when(userRepository.findByEmail(email)).thenReturn(user); // Replace with appropriate User object
//
//
//        assertEquals(email, user.getEmail());
//    }

    @Test
    void getUserRepository() {
        // Act
        UserRepository result = helper.getUserRepository();

        // Assert
        assertEquals(userRepository, result);
    }

    @Test
    void testGetUser() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);


        // Define your test data
        String token = generateAccessToken(60);
        String authorizationHeader = "Bearer " + token;
        String email = "ujohnwesly8@gmail.com";

        // Mock the behavior of the request and JWT.decode
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        DecodedJWT decodedJWT = JWT.decode(token);
        String resultEmail = decodedJWT.getSubject();

        // Mock the UserRepository to return a User
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Call the method to test
        User resultUser = helper.getUser(request);

        // Verify the results
        assertEquals(email, resultEmail);
        assertEquals(user, resultUser);
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
                .sign(algorithm);
    }
}
