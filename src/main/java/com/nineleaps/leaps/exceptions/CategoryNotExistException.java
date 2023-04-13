package com.nineleaps.leaps.exceptions;

public class CategoryNotExistException extends IllegalArgumentException {
    public CategoryNotExistException(String msg) {
        super(msg);
    }
}
