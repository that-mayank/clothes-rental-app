package com.nineleaps.leaps.dto.notifications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PushNotificationResponseTest {

    private PushNotificationResponse pushNotificationResponse;

    @BeforeEach
    void setUp() {
        // Create a PushNotificationResponse object
        pushNotificationResponse = new PushNotificationResponse(200, "OK");
    }

    @Test
    void getStatus() {
        assertEquals(200, pushNotificationResponse.getStatus());
    }

    @Test
    void getMessage() {
        assertEquals("OK", pushNotificationResponse.getMessage());
    }

    @Test
    void setStatus() {
        pushNotificationResponse.setStatus(404);
        assertEquals(404, pushNotificationResponse.getStatus());
    }

    @Test
    void setMessage() {
        pushNotificationResponse.setMessage("Not Found");
        assertEquals("Not Found", pushNotificationResponse.getMessage());
    }
}
