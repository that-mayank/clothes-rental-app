package com.nineleaps.leaps.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseDtoTest {

    private ResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new ResponseDto("Success", "Operation completed successfully");
    }

    @Test
    void getStatus() {
        assertEquals("Success", responseDto.getStatus());
    }

    @Test
    void getMessage() {
        assertEquals("Operation completed successfully", responseDto.getMessage());
    }

    @Test
    void setStatus() {
        responseDto.setStatus("Error");
        assertEquals("Error", responseDto.getStatus());
    }

    @Test
    void setMessage() {
        responseDto.setMessage("An error occurred.");
        assertEquals("An error occurred.", responseDto.getMessage());
    }
}
