package com.nineleaps.leaps.dto.pushnotification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("unit_tests")
@DisplayName("PushNotificationResponse Tests")
class PushNotificationResponseTest {

    @Test
    @DisplayName("Test setter for status")
    void testSetterForStatus() {
        // Arrange
        PushNotificationResponse response = new PushNotificationResponse();
        int status = 200;

        // Act
        response.setStatus(status);

        // Assert
        assertEquals(status, response.getStatus());
    }

    @Test
    @DisplayName("Test setter for message")
    void testSetterForMessage() {
        // Arrange
        PushNotificationResponse response = new PushNotificationResponse();
        String message = "Success";

        // Act
        response.setMessage(message);

        // Assert
        assertEquals(message, response.getMessage());
    }
}
