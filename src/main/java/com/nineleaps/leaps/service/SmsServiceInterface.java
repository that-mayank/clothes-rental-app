package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.otp.SmsPojo;
import com.nineleaps.leaps.model.User;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SmsServiceInterface {
    void send(SmsPojo sms);

    void recieve(MultiValueMap<String, String> smscallback);

    void generateToken(HttpServletResponse response, HttpServletRequest request);

    User user();

}
