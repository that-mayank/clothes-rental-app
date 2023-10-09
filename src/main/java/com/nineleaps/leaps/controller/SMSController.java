package com.nineleaps.leaps.controller;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.exceptions.InvalidOtpException;

import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import com.nineleaps.leaps.utils.SecurityUtility;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor

@Api(tags = "Notifications Api", description = "Contains APIs for sending SMS")
@SuppressWarnings("deprecation")
public class SMSController {

    /**
     * Status Code: 200 - HttpStatus.OK
     * Description: The request was successful, and the response contains the requested data.

     * Status Code: 500 - HttpStatus.INTERNAL_SERVER_ERROR
     * Description: An error occurred on the server and no more specific message is suitable.
     */

    // SMS service for SMS-related operations
    private final SmsServiceInterface smsService;

    // User service for user-related operations
    private final UserServiceInterface userService;

    // SimpMessagingTemplate for sending WebSocket messages
    private final SimpMessagingTemplate webSocket;

    private final SecurityUtility securityUtility;


    // Method to get the timestamp
    private String getTimeStamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    @ApiOperation(value = "Send SMS to phone number")
    @PostMapping(value = "/phoneNo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> smsSubmit(@RequestParam String phoneNumber) {
        // Check if the phone number is in the database
        if (!isPhoneNumberPresent(phoneNumber)) {
            return new ResponseEntity<>(new ApiResponse(false, "Phone number not present in the database"), HttpStatus.NOT_FOUND);
        }

        try {
            sendSms(phoneNumber);
            sendSmsWebSocketMessage(phoneNumber);
            return new ResponseEntity<>(new ApiResponse(true, "OTP sent successfully"), HttpStatus.OK);
        } catch (InvalidOtpException e) {
            return new ResponseEntity<>(new ApiResponse(false, "Invalid OTP"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isPhoneNumberPresent(String phoneNumber) {
        return Helper.notNull(userService.getUserViaPhoneNumber(phoneNumber));
    }

    private void sendSms(String phoneNumber) throws InvalidOtpException {
        try {
            smsService.send(phoneNumber);
        } catch (Exception e) {
            throw new InvalidOtpException("Failed to send SMS.");
        }
    }

    // Private method to send a WebSocket message about the sent SMS
    private void sendSmsWebSocketMessage(String phoneNumber) {
        // Destination for web socket
        String topicDestination = "/lesson/sms";
        webSocket.convertAndSend(topicDestination, getTimeStamp() + ": SMS has been sent to " + phoneNumber);
    }


    // API to verify OTP
    @ApiOperation(value = "Verify OTP")
    @PostMapping(value = "/otp",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> verifyOTP(HttpServletResponse response, HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber, @RequestParam("otp") Integer otp) throws IOException {
        // Verify the OTP
        if(smsService.verifyOtp(phoneNumber, otp)){
            securityUtility.generateToken(response, request, phoneNumber);
        }
        return new ResponseEntity<>(new ApiResponse(true, "OTP is verified"), HttpStatus.OK);
    }
}
