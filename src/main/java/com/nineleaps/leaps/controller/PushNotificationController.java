package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.pushNotification.PushNotificationResponse;
import com.nineleaps.leaps.service.implementation.PushNotificationServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;




@RestController
@AllArgsConstructor
public class PushNotificationController {


    private final PushNotificationServiceImpl pushNotificationService;


    @PostMapping("/notification/token")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    @ApiOperation(value = "Send push notification to device using fcm token")
    public ResponseEntity<PushNotificationResponse> sendTokenNotification(@RequestBody String token) {
        pushNotificationService.sendNotification(token);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."),
                HttpStatus.OK);
    }

}