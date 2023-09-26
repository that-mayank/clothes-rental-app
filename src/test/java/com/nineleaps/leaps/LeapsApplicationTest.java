package com.nineleaps.leaps;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = LeapsApplication.class)
class LeapsApplicationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        // Ensure that the application context loads successfully
    }

    @Test
    void passwordEncoderBeanShouldExist() {
        assertNotNull(passwordEncoder);
    }
}