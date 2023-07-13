package com.nineleaps.leaps.service;

import com.nineleaps.leaps.exceptions.OtpValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface SmsServiceInterface {
    void send(String phoneNumber);
    void verifyOtp(String phoneNumber, Integer otp, HttpServletResponse response, HttpServletRequest request) throws OtpValidationException, IOException;
    void generateToken(HttpServletResponse response, HttpServletRequest request, String phoneNumber) throws IOException;
}