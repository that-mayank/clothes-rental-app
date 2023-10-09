package com.nineleaps.leaps.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
@DisplayName("ResponseDto Tests")
@Tag("unit_tests")
class ResponseDtoTest {

    @Test
    @DisplayName("Test constructor and getters")
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
    @DisplayName("Test setters")
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
