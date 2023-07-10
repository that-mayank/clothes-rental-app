package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthenticationFailExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Authentication failed.";
        AuthenticationFailException exception = new AuthenticationFailException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }
}
