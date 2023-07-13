package com.nineleaps.leaps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LeapsApplicationTests {

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void testPasswordEncoderBean() {
        // Arrange
        LeapsApplication leapsApplication = new LeapsApplication();

        // Act
        PasswordEncoder passwordEncoder = leapsApplication.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }



}
