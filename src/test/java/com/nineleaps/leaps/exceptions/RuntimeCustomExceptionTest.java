package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuntimeCustomExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        String errorMessage = "This is a custom runtime exception";
        RuntimeCustomException exception = new RuntimeCustomException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }
}
