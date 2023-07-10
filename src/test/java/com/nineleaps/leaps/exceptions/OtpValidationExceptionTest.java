package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

 class OtpValidationExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "OTP validation failed.";
        OtpValidationException exception = new OtpValidationException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }
}
