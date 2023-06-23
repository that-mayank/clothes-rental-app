package com.nineleaps.leaps.service;

import com.nineleaps.leaps.exceptions.OtpValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SmsServiceInterface {
    public void send(String phoneNumber);
    void verifyOtp(String phoneNumber, Integer otp, HttpServletResponse response, HttpServletRequest request) throws OtpValidationException;
}