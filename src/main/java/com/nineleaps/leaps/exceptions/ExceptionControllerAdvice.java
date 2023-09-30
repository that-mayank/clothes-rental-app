package com.nineleaps.leaps.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

//it handles all exceptions at global level
@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<String> handleUpdateFailException(CustomException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AuthenticationFailException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public final ResponseEntity<String> handleUpdateFailException(AuthenticationFailException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = CategoryNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<String> handleUpdateFailException(CategoryNotExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ProductNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<String> handleUpdateFailException(ProductNotExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CartItemNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<String> handleUpdateFailException(CartItemNotExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CartItemAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<String> handleUpdateFailException(CartItemAlreadyExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<String> handleUpdateFailException(UserNotExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<String> handleUpdateFailException(OrderNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = QuantityOutOfBoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<String> handleUpdateFailException(QuantityOutOfBoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
