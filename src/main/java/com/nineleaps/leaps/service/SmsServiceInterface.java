package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.otp.SmsPojo;
import com.nineleaps.leaps.model.User;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SmsServiceInterface {
    public void send(SmsPojo sms);
    public void recieve (MultiValueMap<String,String> smscallback);
    public void generateToken(HttpServletResponse response, HttpServletRequest request);

    public User user();

}
