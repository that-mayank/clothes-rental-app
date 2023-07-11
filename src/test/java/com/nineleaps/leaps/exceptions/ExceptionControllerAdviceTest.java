package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionControllerAdviceTest {

    private ExceptionControllerAdvice exceptionControllerAdvice = new ExceptionControllerAdvice();

    @Test
    void handleCustomException_shouldReturnBadRequest() {
        CustomException exception = new CustomException("Custom exception message");

        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Custom exception message", response.getBody());
    }

    @Test
    void handleAuthenticationFailException_shouldReturnBadRequest() {
        AuthenticationFailException exception = new AuthenticationFailException("Authentication fail message");

        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Authentication fail message", response.getBody());
    }


    @Test
    void handleCategoryNotExistException_shouldReturnBadRequest() {
        CategoryNotExistException exception = new CategoryNotExistException("Category does not exist");

        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Category does not exist", response.getBody());
    }

    @Test
    void handleProductNotExistException_shouldReturnBadRequest() {
        ProductNotExistException exception = new ProductNotExistException("Product does not exist");

        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product does not exist", response.getBody());
    }

    @Test
    void handleCartItemNotExistException_shouldReturnBadRequest() {
        CartItemNotExistException exception = new CartItemNotExistException("Cart item does not exist");

        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cart item does not exist", response.getBody());
    }

    @Test
    void handleCartItemAlreadyExistException_shouldReturnBadRequest() {
        CartItemAlreadyExistException exception = new CartItemAlreadyExistException("Cart item already exists");

        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cart item already exists", response.getBody());
    }

    @Test
    void handleUserNotExistException_shouldReturnBadRequest() {
        UserNotExistException exception = new UserNotExistException("User does not exist");

        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User does not exist", response.getBody());
    }

    @Test
    void handleOrderNotFoundException_shouldReturnBadRequest() {
        OrderNotFoundException exception = new OrderNotFoundException("Order not found");

        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Order not found", response.getBody());
    }

    @Test
    void handleQuantityOutOfBoundException_shouldReturnBadRequest() {
        QuantityOutOfBoundException exception = new QuantityOutOfBoundException("Quantity out of bound message");

        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Quantity out of bound message", response.getBody());
    }

    @Test
    void testHandleUpdateFailException() {
        CustomException exception = new CustomException("Custom exception message");
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();
        ResponseEntity<String> response = advice.handleUpdateFailException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Custom exception message", response.getBody());
    }

}
