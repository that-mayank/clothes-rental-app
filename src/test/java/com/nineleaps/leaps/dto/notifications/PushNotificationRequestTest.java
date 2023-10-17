package com.nineleaps.leaps.dto.notifications;

import com.nineleaps.leaps.dto.notifications.PushNotificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PushNotificationRequestTest {

    private PushNotificationRequest pushNotificationRequest;

    @BeforeEach
    void setUp() {
        // Create a PushNotificationRequest object
        pushNotificationRequest = PushNotificationRequest.builder()
                .title("Custom Title")
                .message("Custom Message")
                .topic("Custom Topic")
                .token("Custom Token")
                .build();
    }

    @Test
    void getTitle() {
        assertEquals("Custom Title", pushNotificationRequest.getTitle());
    }

    @Test
    void getMessage() {
        assertEquals("Custom Message", pushNotificationRequest.getMessage());
    }

    @Test
    void getTopic() {
        assertEquals("Custom Topic", pushNotificationRequest.getTopic());
    }

    @Test
    void getToken() {
        assertEquals("Custom Token", pushNotificationRequest.getToken());
    }

    @Test
    void setTitle() {
        pushNotificationRequest.setTitle("New Title");
        assertEquals("New Title", pushNotificationRequest.getTitle());
    }

    @Test
    void setMessage() {
        pushNotificationRequest.setMessage("New Message");
        assertEquals("New Message", pushNotificationRequest.getMessage());
    }

    @Test
    void setTopic() {
        pushNotificationRequest.setTopic("New Topic");
        assertEquals("New Topic", pushNotificationRequest.getTopic());
    }

    @Test
    void setToken() {
        pushNotificationRequest.setToken("New Token");
        assertEquals("New Token", pushNotificationRequest.getToken());
    }

    @Test
    void builder() {
        PushNotificationRequest newRequest = PushNotificationRequest.builder()
                .title("Another Title")
                .message("Another Message")
                .topic("Another Topic")
                .token("Another Token")
                .build();

        assertEquals("Another Title", newRequest.getTitle());
        assertEquals("Another Message", newRequest.getMessage());
        assertEquals("Another Topic", newRequest.getTopic());
        assertEquals("Another Token", newRequest.getToken());
    }
}
