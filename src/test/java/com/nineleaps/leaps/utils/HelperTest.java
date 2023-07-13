package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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
        // Create a non-null object for testing
        Object obj = new Object();

        // Call the notNull() method and assert that it returns true
        assertTrue(Helper.notNull(obj));
    }

    @Test
    void testNotNull_withNullObject() {
        // Create a null object for testing
        Object obj = null;

        // Call the notNull() method and assert that it returns false
        assertFalse(Helper.notNull(obj));
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
}
