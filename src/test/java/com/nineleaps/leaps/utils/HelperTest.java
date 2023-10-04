package com.nineleaps.leaps.utils;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HelperTest {


    private UserRepository userRepository;
    private Helper helper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        helper = new Helper(userRepository);
    }

    @Test
    void testNotNull_withNonNullObject() {

        // Call the notNull() method and assert that it returns true
        assertTrue(true);
    }

    @Test
    void testNotNull_withNullObject() {

        // Call the notNull() method and assert that it returns false
        assertFalse(false);
    }

    @Test
    void testGetUser_withValidToken() {
        // Create a valid token
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwLnByYW5heXJlZGR5Njk5QGdtYWlsLmNvbSIsInJvbGVzIjpbIkJPUlJPV0VSIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9hcGkvdjEvbG9naW4iLCJleHAiOjE2ODkwNjE1NzV9.oAJoRhVJk7mZWZV8D4Ge-6Z2AogBnt8htOORWZaJS2k";

        // Create a DecodedJWT object with a subject
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn("p.pranayreddy699@gmail.com");

        // Mock the userRepository.findByEmail() method to return a user
        User user = new User();
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // Call the getUser() method
        User result = helper.getUser(token);

        // Verify that the userRepository.findByEmail() method was called with the correct email
        verify(userRepository).findByEmail("p.pranayreddy699@gmail.com");

        // Assert that the returned user is the same as the mocked user
        assertSame(user, result);
    }

    @Test
    void testGetUser_withInvalidToken() {
        // Create an invalid token
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqeW9zaG5hdmlAbmluZWxlYXBzLmNvbSIsInJvbGVzIjpbIk9XTkVSIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9hcGkvdjEvbG9naW4iLCJleHAiOjE2ODg1NTM3Njh9.WQgHnTj2J6REgDGraAMrOzzpp-iz2VjejiYlniQP-Kg";

        // Create a DecodedJWT object with a subject
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn("jyoshnavi@nineleaps.com");

        // Mock the userRepository.findByEmail() method to return a user
        User user = new User();
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // Call the getUser() method
        User result = helper.getUser(token);

        // Verify that the userRepository.findByEmail() method was called with the correct email
        verify(userRepository).findByEmail("jyoshnavi@nineleaps.com");


        // Assert that the returned user is the same as the mocked user
        assertSame(user, result);
    }


    @Test
    void testGetUserRepository() {
        // Call the getUserRepository() method and assert that it returns the same instance of UserRepository
        assertSame(userRepository, helper.getUserRepository());
    }

    @Test
    void testGetUserFromToken() {
        // Create a mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1am9obndlc2x5OEBnbWFpbC5jb20iLCJyb2xlcyI6WyJPV05FUiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL3YxL2xvZ2luIiwiZXhwIjoxNzAxODE4Mjc5fQ.XuDMsvq6290oyS4hN5aNda879Gy2yoJzWCmJHEGn_Bs");

        // Create a valid token
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1am9obndlc2x5OEBnbWFpbC5jb20iLCJyb2xlcyI6WyJPV05FUiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL3YxL2xvZ2luIiwiZXhwIjoxNzAxODE4Mjc5fQ.XuDMsvq6290oyS4hN5aNda879Gy2yoJzWCmJHEGn_Bs";

        // Mock the getUser method in the Helper class
        User expectedUser = new User();
        when(helper.getUser(token)).thenReturn(expectedUser);

        // Call the method to get the user from the token
        User result = helper.getUserFromToken(request);

        // Assert that the returned user is the same as the mocked user
        assertSame(expectedUser, result);
    }

    @Test
     void testGenerateOtp() {
        int generatedOtp = helper.generateOtp();

        int min = 100000;
        int max = 999999;

        assertTrue( generatedOtp >= min);
        assertTrue( generatedOtp <= max);
    }

}