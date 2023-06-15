package com.nineleaps.leaps.exceptions;

public class CartItemAlreadyExistException extends IllegalArgumentException {
    public CartItemAlreadyExistException(String msg) {
        super(msg);
    }
}
