package com.nineleaps.leaps.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//it handles all exceptions at global level
@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = CustomException.class)
    public final ResponseEntity<String> handleUpdateFailException(CustomException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AuthenticationFailException.class)
    public final ResponseEntity<String> handleUpdateFailException(AuthenticationFailException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CategoryNotExistException.class)
    public final ResponseEntity<String> handleUpdateFailException(CategoryNotExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ProductNotExistException.class)
    public final ResponseEntity<String> handleUpdateFailException(ProductNotExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CartItemNotExistException.class)
    public final ResponseEntity<String> handleUpdateFailException(CartItemNotExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CartItemAlreadyExistException.class)
    public final ResponseEntity<String> handleUpdateFailException(CartItemAlreadyExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotExistException.class)
    public final ResponseEntity<String> handleUpdateFailException(UserNotExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = OrderNotFoundException.class)
    public final ResponseEntity<String> handleUpdateFailException(OrderNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = QuantityOutOfBoundException.class)
    public final ResponseEntity<String> handleUpdateFailException(QuantityOutOfBoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
