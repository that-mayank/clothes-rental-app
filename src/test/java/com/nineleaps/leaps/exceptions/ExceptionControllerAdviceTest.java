package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionControllerAdviceTest {

    private ExceptionControllerAdvice controllerAdvice;

    @BeforeEach
    void setUp() {
        controllerAdvice = new ExceptionControllerAdvice();
    }

    @Test
    void handleUpdateFailException() {
        CustomException customException = new CustomException("Custom exception message");
        ResponseEntity<String> responseEntity = controllerAdvice.handleUpdateFailException(customException);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Custom exception message", responseEntity.getBody());
    }

    @Test
    void handleCategoryNotExistException() {
        CategoryNotExistException categoryNotExistException = new CategoryNotExistException("Category not found");
        ResponseEntity<String> responseEntity = controllerAdvice.handleUpdateFailException(categoryNotExistException);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Category not found", responseEntity.getBody());
    }

    @Test
    void handleProductNotExistException() {
        ProductNotExistException productNotExistException = new ProductNotExistException("Product not found");
        ResponseEntity<String> responseEntity = controllerAdvice.handleUpdateFailException(productNotExistException);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Product not found", responseEntity.getBody());
    }

    @Test
    void handleCartItemNotExistException() {
        CartItemNotExistException cartItemNotExistException = new CartItemNotExistException("Cart item not found");
        ResponseEntity<String> responseEntity = controllerAdvice.handleUpdateFailException(cartItemNotExistException);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Cart item not found", responseEntity.getBody());
    }

    @Test
    void handleCartItemAlreadyExistException() {
        CartItemAlreadyExistException cartItemAlreadyExistException = new CartItemAlreadyExistException("Cart item already exists");
        ResponseEntity<String> responseEntity = controllerAdvice.handleUpdateFailException(cartItemAlreadyExistException);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals("Cart item already exists", responseEntity.getBody());
    }

    @Test
    void handleUserNotExistException() {
        UserNotExistException userNotExistException = new UserNotExistException("User not found");
        ResponseEntity<String> responseEntity = controllerAdvice.handleUpdateFailException(userNotExistException);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("User not found", responseEntity.getBody());
    }

    @Test
    void handleOrderNotFoundException() {
        OrderNotFoundException orderNotFoundException = new OrderNotFoundException("Order not found");
        ResponseEntity<String> responseEntity = controllerAdvice.handleUpdateFailException(orderNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Order not found", responseEntity.getBody());
    }

    @Test
    void handleQuantityOutOfBoundException() {
        QuantityOutOfBoundException quantityOutOfBoundException = new QuantityOutOfBoundException("Quantity out of bound");
        ResponseEntity<String> responseEntity = controllerAdvice.handleUpdateFailException(quantityOutOfBoundException);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Quantity out of bound", responseEntity.getBody());
    }

    @Test
    void handleAddressOwnershipException() {
        AddressOwnershipException addressOwnershipException = new AddressOwnershipException("Address ownership exception");
        ResponseEntity<String> responseEntity = controllerAdvice.handleUpdateFailException(addressOwnershipException);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals("Address ownership exception", responseEntity.getBody());
    }
}
