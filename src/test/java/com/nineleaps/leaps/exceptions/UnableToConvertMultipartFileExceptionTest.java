package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnableToConvertMultipartFileExceptionTest {

    @Test
    void constructorShouldThrowExceptionWithMessage() {
        String message = "Test exception message";

        try {
            throw new UnableToConvertMultipartFileException(message);
        } catch (UnableToConvertMultipartFileException e) {
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    void constructorShouldThrowExceptionWithMessageAndCause() {
        String message = "Test exception message";
        Throwable cause = new RuntimeException("Test cause");

        try {
            throw new UnableToConvertMultipartFileException(message, cause);
        } catch (UnableToConvertMultipartFileException e) {
            assertEquals(message, e.getMessage());
            assertEquals(cause, e.getCause());
        }
    }
}
