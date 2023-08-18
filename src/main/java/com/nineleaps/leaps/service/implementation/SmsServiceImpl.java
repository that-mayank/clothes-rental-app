package com.nineleaps.leaps.service.implementation;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nineleaps.leaps.exceptions.OtpValidationException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.SmsServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.SecurityUtility;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
@Service
@RequiredArgsConstructor
@Transactional
public class SmsServiceImpl implements SmsServiceInterface {
    @Value("${twilio.account_sid}")
    private String accountSid;
    @Value("${twilio.token}")
    private String authToken;
    @Value("${twilio.from_number}")
    private String fromNumber;
    private final UserServiceInterface userServiceInterface;
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
            generateToken(response, request, phoneNumber);
            otpMap.remove(phoneNumber);
        } else if (!Objects.equals(otpMap.get(phoneNumber), otp)) {
            throw new OtpValidationException("OTP not valid for phone number");
        }
    }
    public User user(String phoneNumber) {
        return userServiceInterface.getUserViaPhoneNumber(phoneNumber);
    }

    @Override
    public void generateToken(HttpServletResponse response, HttpServletRequest request, String phoneNumber) throws IOException {
        String secretFilePath = "Desktop/leaps/secret/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
        String secret = securityUtility.readSecretFromFile(absolutePath);
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        String role = user(phoneNumber).getRole().toString();
        String[] roles = new String[]{role};
        String email = user(phoneNumber).getEmail();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpirationTime = now.plusMinutes(1440); // Update to desired expiration time
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime refreshTokenExpirationTime = now.plusMinutes(43200); // Update to desired expiration time 30 days
        Date refreshTokenExpirationDate = Date.from(refreshTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        String accessToken = JWT.create()
                .withSubject(email)
                .withExpiresAt(accessTokenExpirationDate)
                .withClaim("roles", Arrays.asList(roles))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(user(phoneNumber).getEmail())
                .withExpiresAt(refreshTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        response.setHeader("access_token", accessToken);
        response.setHeader("refresh_token", refreshToken);
        securityUtility.saveTokens(refreshToken, email);
    }



}


