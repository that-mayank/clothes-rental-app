package com.nineleaps.leaps.service.implementation;
import com.nineleaps.leaps.service.SmsSender;
import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.*;
@Service
@Transactional
public class SmsServiceImpl implements SmsServiceInterface {

    final SmsSender smsSender;
    final Helper helper;
    final Map<String, Integer> otpMap;

    public SmsServiceImpl(SmsSender smsSender, Helper helper) {
        this.smsSender = smsSender;
        this.helper = helper;
        this.otpMap = new HashMap<>();
    }


    @Override
    public void send(String phoneNumber) {
        int otp = helper.generateOtp();
        String msg = "Your OTP - " + otp + " please verify this otp";
        smsSender.sendSms("+91" + phoneNumber, msg);
        otpMap.put(phoneNumber, otp);
    }

    @Override
    public boolean verifyOtp(String phoneNumber, Integer otp) {
        Integer storedOtp = otpMap.get(phoneNumber);
        return storedOtp != null && Objects.equals(storedOtp, otp);
    }


}



