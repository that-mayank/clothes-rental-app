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


    // Method to get the timestamp
    private String getTimeStamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    // API to send an SMS to a phone number
    @ApiOperation(value = "Send SMS to phone number")
    @PostMapping("/phoneNo")
    public ResponseEntity<ApiResponse> smsSubmit(@RequestParam String phoneNumber) {
        // Check if the phone number is in the database
        if (!Helper.notNull(userService.getUserViaPhoneNumber(phoneNumber))) {
            return new ResponseEntity<>(new ApiResponse(false, "Phone number not present in the database"), HttpStatus.NOT_FOUND);
        }
        try {
            // Send the SMS
            smsService.send(phoneNumber);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "Enter a valid OTP"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Send a WebSocket message about the sent SMS
        // Destination for web socket
        String topicDestination = "/lesson/sms";
        webSocket.convertAndSend(topicDestination, getTimeStamp() + ": SMS has been sent to " + phoneNumber);
        return new ResponseEntity<>(new ApiResponse(true, "OTP sent successfully"), HttpStatus.OK);
    }

    // API to verify OTP
    @ApiOperation(value = "Verify OTP")
    @PostMapping("/otp")
    public ResponseEntity<ApiResponse> verifyOTP(HttpServletResponse response, HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber, @RequestParam("otp") Integer otp) throws OtpValidationException, IOException {
        // Verify the OTP
        smsService.verifyOtp(phoneNumber, otp, response, request);
        return new ResponseEntity<>(new ApiResponse(true, "OTP is verified"), HttpStatus.OK);
    }
}
