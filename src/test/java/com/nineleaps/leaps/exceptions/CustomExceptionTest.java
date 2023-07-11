package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CustomExceptionTest {

    @Test
    void testHandleCustomException() {
        CustomException exception = new CustomException("Custom exception message");
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();
        ResponseEntity<String> response = advice.handleUpdateFailException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Custom exception message", response.getBody());
    }

}
