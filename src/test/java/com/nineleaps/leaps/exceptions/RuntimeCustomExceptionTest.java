package com.nineleaps.leaps.exceptions;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@Tag("unit_tests")
class RuntimeCustomExceptionTest {

    @Test
    @DisplayName("RuntimeCustomException Handling")
    void constructorShouldThrowExceptionWithMessage() {
        String message = "Test exception message";

        try {
            throw new RuntimeCustomException(message);
        } catch (RuntimeCustomException e) {
            assertEquals(message, e.getMessage());
        }
    }
}
