package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.service.implementation.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EmailServiceImplTest {
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl();
    }

    @ParameterizedTest
    @CsvSource({
            "Test Subject, Test Message, recipient@example.com, false",
            "Test Subject, Test Message, , false",
            "Test Subject, Test Message, invalid_email_address, false",
            ", Test Message, recipient@example.com, false"
    })
    void sendEmail_TestCases(
            String subject, String message, String to, boolean expected) {
        // Act
        boolean result = emailService.sendEmail(subject, message, to);
        // Assert
        assertFalse(result);
    }
}
