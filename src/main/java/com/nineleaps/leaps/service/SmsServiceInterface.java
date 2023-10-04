package com.nineleaps.leaps.service;

public interface SmsServiceInterface {
    void send(String phoneNumber);
    boolean verifyOtp(String phoneNumber, Integer otp);
}