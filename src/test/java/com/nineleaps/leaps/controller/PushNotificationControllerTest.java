//package com.nineleaps.leaps.controller;
//
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.dto.pushnotification.PushNotificationResponse;
//import com.nineleaps.leaps.service.implementation.PushNotificationServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@Tag("unit_tests")
//@DisplayName("Test case file for Push Notification Controller")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//class PushNotificationControllerTest {
//    @Mock
//    private PushNotificationServiceImpl pushNotificationService;
//
//    @InjectMocks
//    private PushNotificationController pushNotificationController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);}
//
//    @Test
//    @DisplayName("send Notification")
//    void sendTokenNotification() {
//        // Mock the FCM token
//        String token = "sampleFCMToken";
//
//        // Mock the push notification service behavior
//        doNothing().when(pushNotificationService).sendNotification(token);
//
//        // Call the controller method
//        ResponseEntity<PushNotificationResponse> responseEntity = pushNotificationController.sendTokenNotification(token);
//
//        // Verify the response
//        assertNotNull(responseEntity);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//
//        PushNotificationResponse responseBody = responseEntity.getBody();
//        assertNotNull(responseBody);
//        assertEquals(HttpStatus.OK.value(), responseBody.getStatus());
//        assertEquals("Notification has been sent.", responseBody.getMessage());
//
//        // Verify that the push notification service was called
//        verify(pushNotificationService).sendNotification(token);
//    }
//
//
//    @Test
//    @DisplayName("Send token get 200 OK")
//     void test_api_call_with_valid_token_returns_http_status_ok() {
//        // Mock the pushNotificationService
//        PushNotificationServiceImpl pushNotificationService = mock(PushNotificationServiceImpl.class);
//
//        // Create the PushNotificationController instance with the mocked pushNotificationService
//        PushNotificationController pushNotificationController = new PushNotificationController(pushNotificationService);
//
//        // Create the request body with a valid token
//        String token = "valid_token";
//
//        // Invoke the sendTokenNotification API
//        ResponseEntity<PushNotificationResponse> response = pushNotificationController.sendTokenNotification(token);
//
//        // Verify that the pushNotificationService.sendNotification method was called with the correct token
//        verify(pushNotificationService).sendNotification(token);
//
//        // Verify that the response status code is HttpStatus.OK
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("send successful notification")
//     void test_push_notification_service_successfully_sends_notification() {
//        // Mock the pushNotificationService
//        PushNotificationServiceImpl pushNotificationService = mock(PushNotificationServiceImpl.class);
//
//        // Create the PushNotificationController instance with the mocked pushNotificationService
//        PushNotificationController pushNotificationController = new PushNotificationController(pushNotificationService);
//
//        // Create a valid token
//        String token = "valid_token";
//
//        // Invoke the sendTokenNotification API
//        ResponseEntity<PushNotificationResponse> response = pushNotificationController.sendTokenNotification(token);
//
//        // Verify that the pushNotificationService.sendNotification method was called with the correct token
//        verify(pushNotificationService).sendNotification(token);
//
//        // Verify that the response status code is HttpStatus.OK
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }
//
//
//}

package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.pushnotification.PushNotificationResponse;
import com.nineleaps.leaps.service.implementation.PushNotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
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
    @DisplayName("Send Notification")
    void sendTokenNotification() {
        // Arrange
        String deviceToken = "device_token";

        // Act
        ResponseEntity<PushNotificationResponse> response = pushNotificationController.sendTokenNotification(deviceToken);

        // Assert
        PushNotificationResponse responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseBody != null;
        assertEquals("Notification has been sent.", responseBody.getMessage());
        // Verify that the service method was called with the correct device token
        verify(pushNotificationService, times(1)).sendNotification(deviceToken);
    }
}