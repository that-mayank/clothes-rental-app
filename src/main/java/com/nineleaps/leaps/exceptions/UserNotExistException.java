package com.nineleaps.leaps.exceptions;

public class UserNotExistException extends IllegalArgumentException {
    public UserNotExistException(String msg) {
        super(msg);
    }
}
