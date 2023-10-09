package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("unit_tests")
@DisplayName("UnableToConvertMultipartFileException Tests")
class UnableToConvertMultipartFileExceptionTest {

    @Test
    @DisplayName("Constructor should throw exception with message")
    void constructorShouldThrowExceptionWithMessage() {
        String message = "Test exception message";

        try {
            throw new UnableToConvertMultipartFileException(message);
        } catch (UnableToConvertMultipartFileException e) {
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    @DisplayName("Constructor should throw exception with message and cause")
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
