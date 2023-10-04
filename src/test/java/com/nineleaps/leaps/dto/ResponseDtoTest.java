package com.nineleaps.leaps.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
class ResponseDtoTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String status = "success";
        String message = "Operation completed successfully";

        // Act
        ResponseDto responseDto = new ResponseDto(status, message);

        // Assert
        assertEquals(status, responseDto.getStatus());
        assertEquals(message, responseDto.getMessage());
    }

    @Test
    void testSetters() {
        // Arrange
        ResponseDto responseDto = new ResponseDto();
        String newStatus = "failure";
        String newMessage = "Operation failed";

        // Act
        responseDto.setStatus(newStatus);
        responseDto.setMessage(newMessage);

        // Assert
        assertEquals(newStatus, responseDto.getStatus());
        assertEquals(newMessage, responseDto.getMessage());
    }
}
