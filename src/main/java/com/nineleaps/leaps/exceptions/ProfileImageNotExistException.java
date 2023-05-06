package com.nineleaps.leaps.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProfileImageNotExistException extends IllegalArgumentException {
    public ProfileImageNotExistException(String msg) {
        super(msg);
    }
}
