package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidOtpExceptionTest {

    @Test
     void testConstructorWithMessage() {
        String message = "Test exception message";
        InvalidOtpException exception = new InvalidOtpException(message);

        // Verify that the exception message is as expected
        assertEquals(message, exception.getMessage());
    }

}