package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.pushNotification.PushNotificationResponse;
import com.nineleaps.leaps.service.implementation.PushNotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PushNotificationControllerTest {
    @Mock
    private PushNotificationServiceImpl pushNotificationService;

    @InjectMocks
    private PushNotificationController pushNotificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);}

    @Test
    void sendTokenNotification() {
        // Mock the FCM token
        String token = "sampleFCMToken";

        // Mock the push notification service behavior
        doNothing().when(pushNotificationService).sendNotification(token);

        // Call the controller method
        ResponseEntity<PushNotificationResponse> responseEntity = pushNotificationController.sendTokenNotification(token);

        // Verify the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        PushNotificationResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK.value(), responseBody.getStatus());
        assertEquals("Notification has been sent.", responseBody.getMessage());

        // Verify that the push notification service was called
        verify(pushNotificationService).sendNotification(token);
    }

}