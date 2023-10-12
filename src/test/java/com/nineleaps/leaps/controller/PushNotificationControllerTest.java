package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.notifications.PushNotificationResponse;
import com.nineleaps.leaps.service.implementation.PushNotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PushNotificationControllerTest {

    @Mock
    private PushNotificationServiceImpl pushNotificationService;

    @InjectMocks
    private PushNotificationController pushNotificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendTokenNotification() {
        // Arrange
        String deviceToken = "device_token";

        // Act
        ResponseEntity<PushNotificationResponse> response = pushNotificationController.sendTokenNotification(deviceToken);

        // Assert
        PushNotificationResponse responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notification has been sent.", responseBody.getMessage());

        // Verify that the service method was called with the correct device token
        verify(pushNotificationService, times(1)).sendNotification(deviceToken);
    }
}
