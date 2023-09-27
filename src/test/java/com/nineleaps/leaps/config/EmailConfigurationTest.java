package com.nineleaps.leaps.config;

import com.nineleaps.leaps.config.EmailConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class EmailConfigurationTest {

    @Test
    void javaMailSender() {
        EmailConfiguration emailConfiguration = new EmailConfiguration();
        emailConfiguration.setHost("smtp.example.com");
        emailConfiguration.setPort(587);
        emailConfiguration.setUsername("test@example.com");
        emailConfiguration.setPassword("password");
        emailConfiguration.setSender("test@example.com");

        JavaMailSender javaMailSender = emailConfiguration.javaMailSender();

        // Assert that the JavaMailSender is not null
        assertNotNull(javaMailSender);
    }
}
