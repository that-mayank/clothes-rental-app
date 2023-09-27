package com.nineleaps.leaps.exceptions;



public class UnableToConvertMultipartFileException extends RuntimeException {

    public UnableToConvertMultipartFileException(String message) {
        super(message);
    }

    public UnableToConvertMultipartFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
