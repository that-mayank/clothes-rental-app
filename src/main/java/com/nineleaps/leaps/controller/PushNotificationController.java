package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.pushNotification.PushNotificationResponse;
import com.nineleaps.leaps.service.implementation.PushNotificationServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PushNotificationController {

    // Status Code: 200 - HttpStatus.OK
    // Description: The request was successful, and the response contains the requested data.

    // Service responsible for push notifications
    private final PushNotificationServiceImpl pushNotificationService;

    // API to send push notification to a device using FCM token
    @PostMapping(value = "/notification/token",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority( 'BORROWER')")
    @ApiOperation(value = "Send push notification to device using fcm token")
    public ResponseEntity<PushNotificationResponse> sendTokenNotification(@RequestBody String token) {
        // Invoke the push notification service to send notification using the provided token
        pushNotificationService.sendNotification(token);

        // Return a response indicating the notification has been sent successfully
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."),
                HttpStatus.OK);
    }
}
