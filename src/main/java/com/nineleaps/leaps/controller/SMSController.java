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
@Api(tags = "Notifications Api", description = "Contains api for sending sms")
@SuppressWarnings("deprecation")
public class SMSController {

    private final SmsServiceInterface smsService;
    private String topicDestination = "/lesson/sms";

    private final UserServiceInterface userService;

    private final SimpMessagingTemplate webSocket;

    private String getTimeStamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    @ApiOperation(value = "Send sms to phone number")
    @PostMapping("/phoneNo")
    public ResponseEntity<ApiResponse> smsSubmit(@RequestParam String phoneNumber) {
        //if the phoneNumber is in database or not
        if (!Helper.notNull(userService.getUserViaPhoneNumber(phoneNumber))) {
            return new ResponseEntity<>(new ApiResponse(false, "Phone number not present in database"), HttpStatus.NOT_FOUND);
        }
        try {
            smsService.send(phoneNumber);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "Enter a valid OTP"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        webSocket.convertAndSend(topicDestination, getTimeStamp() + ":SMS has been sent " + phoneNumber);
        return new ResponseEntity<>(new ApiResponse(true, "OTP sent successfully"), HttpStatus.OK);

    }

    @ApiOperation(value = "Verify otp")
    @PostMapping("/otp")
    public ResponseEntity<ApiResponse> verifyOTP(HttpServletResponse response, HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber, @RequestParam("otp") Integer otp) throws OtpValidationException, IOException {
        smsService.verifyOtp(phoneNumber, otp, response, request);
        return new ResponseEntity<>(new ApiResponse(true, "OTP is verified"), HttpStatus.OK);

    }

}