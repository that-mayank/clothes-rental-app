package com.nineleaps.leaps.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nineleaps.leaps.config.Filter.CustomAuthenticationFilter;
import com.nineleaps.leaps.dto.otp.SmsPojo;
import com.nineleaps.leaps.dto.otp.StoreOTP;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.utils.SecurityUtility;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class SmsService implements SmsServiceInterface {
    @Value("${twilio.account_sid}")
    private String ACCOUNT_SID;
    @Value("${twilio.token}")
    private String AUTH_TOKEN;
    @Value("${twilio.from_number}")
    private String FROM_NUMBER;
    private String phoneNumber = null;

    private final UserServiceInterface userServiceInterface;
    private final SecurityUtility securityUtility;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    //method to send otp to phonenumber
    public void send(SmsPojo sms){
        Twilio.init(ACCOUNT_SID,AUTH_TOKEN);

        int min = 100000;
        int max = 999999;
        int number  = (int)(Math.random()*(max-min +1)+min);

        String msg = "Your OTP - "+number+"please verify this otp";

        Message message = Message.creator(new PhoneNumber("+91"+sms.getPhoneNo()),new PhoneNumber(FROM_NUMBER),msg).create();
        StoreOTP.setOtp(number);
        phoneNumber = sms.setStorePhoneNo(sms.getPhoneNo());

    }
    public void recieve (MultiValueMap<String,String> smscallback){

    }


    //get user details via phonenumber
    public  User user(){
        return userServiceInterface.getUserViaPhoneNumber(getPhoneNumber());}


    //generate token while logging through phonenumber
    public  void generateToken(HttpServletResponse response, HttpServletRequest request){
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String role = user().getRole().toString();
        String[] roles = new String[]{role};
        String email = user().getEmail();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpirationTime = now.plusMinutes(1440); // Update to desired expiration time
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());

        LocalDateTime refreshTokenExpirationTime = now.plusMinutes(43200); // Update to desired expiration time 30 days
        Date refreshTokenExpirationDate = Date.from(refreshTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());

        String access_token = JWT.create()
                .withSubject(email)
                .withExpiresAt(accessTokenExpirationDate)
                .withClaim ( "roles", Arrays.asList(roles))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        String refresh_token = JWT.create()
                .withSubject(user().getEmail())
                .withExpiresAt(refreshTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        response.setHeader("access_token",access_token);
        response.setHeader("refresh_token",refresh_token);
        securityUtility.saveTokens(refresh_token,email);

    }


}
