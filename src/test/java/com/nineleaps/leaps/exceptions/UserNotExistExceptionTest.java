package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserNotExistExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        String errorMessage = "User does not exist";
        UserNotExistException exception = new UserNotExistException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }
}
