package com.nineleaps.leaps.dto.pushnotification;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
class PushNotificationResponseTest {

    @Test
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
