package com.nineleaps.leaps.controller;


import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.otp.SmsPojo;
import com.nineleaps.leaps.dto.otp.StoreOTP;
import com.nineleaps.leaps.dto.otp.TempOTP;

import com.nineleaps.leaps.service.SmsServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Api(tags = "Notifications Api", description = "Contains api for sending sms")
public class SMSController {
    private final SmsServiceInterface smsServiceInterface;
    private final String TOPIC_DESTINATION = "/lesson/sms";
    private final SimpMessagingTemplate webSocket;

    private String getTimeStamp(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    @ApiOperation(value = "Send sms to phone number")
    @PostMapping("/phoneNo")
    public ResponseEntity<ApiResponse> smsSubmit(@RequestBody SmsPojo sms){
        try{
            smsServiceInterface.send(sms);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<ApiResponse>(new ApiResponse(false, "enter a valid phoneNo"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        webSocket.convertAndSend(TOPIC_DESTINATION,getTimeStamp()+":SMS has been sent "+sms.getPhoneNo());
        return new ResponseEntity<ApiResponse>(new ApiResponse(true, "OTP sent successfully"), HttpStatus.OK);

    }

    @RequestMapping(value = "/smscallback",method= RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)

    public void smsCallback(@RequestBody MultiValueMap<String,String>map){
        smsServiceInterface.recieve(map);
        webSocket.convertAndSend(TOPIC_DESTINATION,getTimeStamp() +":Twilio has made a call");
    }

    @ApiOperation(value = "Send otp api to mobile number")
    @PostMapping("/otp")
    public ResponseEntity<ApiResponse> verifyOTP(@RequestBody TempOTP otp, HttpServletResponse response, HttpServletRequest request) {

        if (otp.getOtp() == StoreOTP.getOtp()){
            smsServiceInterface.generateToken(response,request);
            return new ResponseEntity<ApiResponse>(new ApiResponse(true, "OTP verified"), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Enter a valid OTP"), HttpStatus.BAD_REQUEST);}


    }
}
