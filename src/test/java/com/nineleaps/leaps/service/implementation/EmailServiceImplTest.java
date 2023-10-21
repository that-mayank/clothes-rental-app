package com.nineleaps.leaps.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class EmailServiceImplTest {
    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "Test Subject, Test Message, recipient@example.com, true",
            "Test Subject, Test Message, , false",
            "Test Subject, Test Message, invalid_email_address, false",
            ", Test Message, recipient@example.com, true"
    })
    void sendEmail_TestCases(
            String subject, String message, String to, boolean expected) {
        // Act
        boolean result = emailService.sendEmail(subject, message, to);
        // Assert
        assertEquals(expected, result);
    }
}
