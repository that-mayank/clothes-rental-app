package com.nineleaps.leaps.config;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
@Tag("unit_tests")
class EmailConfigurationTest {

    @Test
    @DisplayName("Test case for setting email configuration")
    void javaMailSender() {
        EmailConfiguration emailConfiguration = new EmailConfiguration();
        emailConfiguration.setHost("smtp.example.com");
        emailConfiguration.setPort(587);
        emailConfiguration.setUsername("test@example.com");
        emailConfiguration.setPassword("password");
        emailConfiguration.setSender("test@example.com");

        JavaMailSender javaMailSender = emailConfiguration.javaMailSender();

        // Assert that the JavaMailSender is not null
        Assertions.assertEquals("smtp.example.com", emailConfiguration.getHost());
        Assertions.assertEquals(587, emailConfiguration.getPort());
        Assertions.assertEquals("test@example.com", emailConfiguration.getUsername());
        Assertions.assertEquals("password", emailConfiguration.getPassword());
        Assertions.assertEquals("test@example.com", emailConfiguration.getSender());
    }
}
