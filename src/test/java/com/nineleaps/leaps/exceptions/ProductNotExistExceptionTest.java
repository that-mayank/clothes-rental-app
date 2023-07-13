package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductNotExistExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        String errorMessage = "Product does not exist";
        ProductNotExistException exception = new ProductNotExistException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }
}
