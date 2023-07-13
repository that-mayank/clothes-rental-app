package com.nineleaps.leaps.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseDtoTest {

    @Test
    void testResponseDtoClass() {
        // Create a ResponseDto object
        ResponseDto responseDto = new ResponseDto("success", "Operation completed successfully");

        // Verify the values using assertions
        assertThat(responseDto.getStatus()).isEqualTo("success");
        assertThat(responseDto.getMessage()).isEqualTo("Operation completed successfully");
    }

    @Test
    void testSetStatus() {
        // Arrange
        String status = "SUCCESS";
        String message = "Operation completed successfully";
        ResponseDto responseDto = new ResponseDto(status,message);

        // Act
        responseDto.setStatus(status);

        // Assert
        assertEquals(status, responseDto.getStatus());
    }

    @Test
    void testSetMessage() {
        // Arrange
        String status = "SUCCESS";
        String message = "Operation completed successfully";
        ResponseDto responseDto = new ResponseDto(status,message);

        // Act
        responseDto.setMessage(message);

        // Assert
        assertEquals(message, responseDto.getMessage());
    }
}

