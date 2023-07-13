package com.nineleaps.leaps.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CategoryNotExistExceptionTest {

    @Test
    void testHandleCategoryNotExistException() {
        CategoryNotExistException exception = new CategoryNotExistException("Category does not exist");
        ExceptionControllerAdvice advice = new ExceptionControllerAdvice();
        ResponseEntity<String> response = advice.handleUpdateFailException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Category does not exist", response.getBody());
    }


}