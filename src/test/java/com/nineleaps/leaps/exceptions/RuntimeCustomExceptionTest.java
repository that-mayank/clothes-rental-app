package com.nineleaps.leaps.exceptions;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuntimeCustomExceptionTest {

    @Test
    void constructorShouldThrowExceptionWithMessage() {
        String message = "Test exception message";

        try {
            throw new RuntimeCustomException(message);
        } catch (RuntimeCustomException e) {
            assertEquals(message, e.getMessage());
        }
    }
}
