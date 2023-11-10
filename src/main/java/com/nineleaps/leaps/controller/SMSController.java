package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
@Validated
@Api(tags = "Sms Api")
public class SMSController {

    //Linking layers using constructor injection
    private final SmsServiceInterface smsService;
    private final UserServiceInterface userService;
    private final SimpMessagingTemplate webSocket;

    private String getTimeStamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    // API : To send sms to phone number
    @ApiOperation(value = "API : To send sms to phone number")
    @PostMapping(value = "{phone}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> smsSubmit(@PathVariable("phone") String phoneNumber) {

        // Guard Statement : If the phone number is in database or not
        if (Optional.ofNullable(userService.getUserViaPhoneNumber(phoneNumber)).isEmpty()) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Phone number not present in database"),
                    HttpStatus.NOT_FOUND);
        }
        try {
            // Send SMS using the smsService
            smsService.send(phoneNumber);
        } catch (Exception e) {
            // Handle exceptions when sending SMS
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "Enter a valid OTP"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Define a topic destination for WebSocket notification
        String topicDestination = "/lesson/sms";

        // Send a WebSocket notification about the SMS sent
        webSocket.convertAndSend(
                topicDestination,
                getTimeStamp() + ":SMS has been sent " + phoneNumber
        );

        // Return a success response
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "OTP sent successfully"),
                HttpStatus.CREATED);
    }

    // API : To verify OTP sent to phone number
    @ApiOperation(value = "API : To verify OTP sent to phone number")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> verifyOTP(
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("otp") Integer otp
    ) throws OtpValidationException, IOException {

        // Call the smsService to verify the OTP
        smsService.verifyOtp(phoneNumber, otp, response, request);

        // Return a success response
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "OTP is verified"),
                HttpStatus.OK);
    }

}