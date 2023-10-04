package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.service.SmsSender;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsSender implements SmsSender {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.token}")
    private String authToken;

    @Value("${twilio.from_number}")
    private String number;

    @Override
    public void sendSms(String phoneNumber, String message) {
        Twilio.init(accountSid, authToken);
        Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(number), message).create();
    }
}

