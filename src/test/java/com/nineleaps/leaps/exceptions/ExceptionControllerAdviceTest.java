package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionControllerAdviceTest {

    private final ExceptionControllerAdvice exceptionControllerAdvice = new ExceptionControllerAdvice();

    @Test
    void handleUpdateFailException_CustomException() {
        CustomException customException = new CustomException("Custom exception message");
        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(customException);
        assertEquals("Custom exception message", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleUpdateFailException_AuthenticationFailException() {
        AuthenticationFailException authenticationFailException = new AuthenticationFailException("Authentication failure");
        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(authenticationFailException);
        assertEquals("Authentication failure", response.getBody());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }


    @Test
    void handleUpdateFailException_CategoryNotExistException() {
        CategoryNotExistException categoryNotExistException = new CategoryNotExistException("Category not found");
        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(categoryNotExistException);
        assertEquals("Category not found", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleUpdateFailException_ProductNotExistException() {
        ProductNotExistException productNotExistException = new ProductNotExistException("Product not found");
        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(productNotExistException);
        assertEquals("Product not found", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleUpdateFailException_CartItemNotExistException() {
        CartItemNotExistException cartItemNotExistException = new CartItemNotExistException("Cart item not found");
        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(cartItemNotExistException);
        assertEquals("Cart item not found", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleUpdateFailException_CartItemAlreadyExistException() {
        CartItemAlreadyExistException cartItemAlreadyExistException = new CartItemAlreadyExistException("Cart item already exists");
        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(cartItemAlreadyExistException);
        assertEquals("Cart item already exists", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleUpdateFailException_UserNotExistException() {
        UserNotExistException userNotExistException = new UserNotExistException("User not found");
        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(userNotExistException);
        assertEquals("User not found", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleUpdateFailException_OrderNotFoundException() {
        OrderNotFoundException orderNotFoundException = new OrderNotFoundException("Order not found");
        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(orderNotFoundException);
        assertEquals("Order not found", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleUpdateFailException_QuantityOutOfBoundException() {
        QuantityOutOfBoundException quantityOutOfBoundException = new QuantityOutOfBoundException("Quantity out of bounds");
        ResponseEntity<String> response = exceptionControllerAdvice.handleUpdateFailException(quantityOutOfBoundException);
        assertEquals("Quantity out of bounds", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ...
}
