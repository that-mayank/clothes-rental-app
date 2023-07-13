package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CartItemNotExistExceptionTest {

    @Test
    void testHandleCartItemAlreadyExistException() {
        CartItemAlreadyExistException exception = new CartItemAlreadyExistException("Cart item already exists");
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();
        ResponseEntity<String> response = advice.handleUpdateFailException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cart item already exists", response.getBody());
    }

}
