package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.utils.SecurityUtility;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
@Service
@Transactional
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsServiceInterface {
    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.token}")
    private String authToken;

    @Value("${twilio.from_number}")
    private String fromNumber;

    private final SecurityUtility securityUtility;
    private Map<String, Integer> otpMap = new HashMap<>();
    private static final int MIN = 100000;
    private static final int MAX = 999999;


    //method to send otp to phone number
    public void send(String phoneNumber) {
        Twilio.init(accountSid, authToken);
        SecureRandom secureRandom = new SecureRandom();
        int otp = secureRandom.nextInt(MAX - MIN + 1) + MIN;
        String msg = "Your OTP - " + otp + " please verify this otp";
        Message.creator(new PhoneNumber("+91" + phoneNumber), new PhoneNumber(fromNumber), msg).create();
        otpMap.put(phoneNumber, otp);
    }
    @Override
    public void verifyOtp(String phoneNumber, Integer otp, HttpServletResponse response, HttpServletRequest request) throws OtpValidationException, IOException {
        if (!otpMap.containsKey(phoneNumber)) {
            throw new OtpValidationException("OTP not generated for phone number");
        } else if (Objects.equals(otpMap.get(phoneNumber), otp)) {
            securityUtility.generateToken(response, request, phoneNumber);
            otpMap.remove(phoneNumber);
        } else if (!Objects.equals(otpMap.get(phoneNumber), otp)) {
            throw new OtpValidationException("OTP not valid for phone number");
        }
    }


}


