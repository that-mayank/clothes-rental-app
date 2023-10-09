package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@Tag("unit_tests")
class InvalidOtpExceptionTest {

    @Test
    @DisplayName("InvalidOtpException Handling")
     void testConstructorWithMessage() {
        String message = "Test exception message";
        InvalidOtpException exception = new InvalidOtpException(message);

        // Verify that the exception message is as expected
        assertEquals(message, exception.getMessage());
    }

}