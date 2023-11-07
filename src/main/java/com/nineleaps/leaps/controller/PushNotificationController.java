package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.notifications.PushNotificationResponse;
import com.nineleaps.leaps.service.implementation.PushNotificationServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/push")
@Validated
@AllArgsConstructor
@Api(tags = "Push Notification Api")
public class PushNotificationController {

    //Linking layers using constructor injection
    private final PushNotificationServiceImpl pushNotificationService;

    // API : To send push notification to device using fcm token
    @ApiOperation(value = "API : To send push notification to device using fcm token")
    @PostMapping("{token}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")
    public ResponseEntity<PushNotificationResponse> sendTokenNotification(
            @PathVariable("token") String deviceToken) {
        // Calling Service layer to send notification
        pushNotificationService.sendNotification(deviceToken);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }
}