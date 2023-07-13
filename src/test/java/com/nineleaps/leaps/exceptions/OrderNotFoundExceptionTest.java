package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderNotFoundExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        String errorMessage = "Order not found";
        OrderNotFoundException exception = new OrderNotFoundException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }
}
