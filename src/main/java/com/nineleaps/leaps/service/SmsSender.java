package com.nineleaps.leaps.service;

public interface SmsSender {
    void sendSms(String phoneNumber, String message);
}
