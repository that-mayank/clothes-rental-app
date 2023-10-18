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
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service // Marks this class as a Spring service component
@RequiredArgsConstructor // Lombok's annotation to generate a constructor with all required fields
@Transactional // Marks this class as transactional for database operations
public class SmsServiceImpl implements SmsServiceInterface {

    // Read Twilio configuration values from application.properties
    @Value("${twilio.account_sid}")
    private String accountSid;
    @Value("${twilio.token}")
    private String authToken;
    @Value("${twilio.from_number}")
    private String fromNumber;

    private final UserServiceInterface userServiceInterface;
    private final SecurityUtility securityUtility;
    Map<String, Integer> otpMap = new HashMap<>();
    private static final int MIN = 100000;
    private static final int MAX = 999999;

    // Method to send an OTP to a phone number
    public void send(String phoneNumber) {
        Twilio.init(accountSid, authToken);
        SecureRandom secureRandom = new SecureRandom();
        int otp = secureRandom.nextInt(MAX - MIN + 1) + MIN;
        String msg = "Your OTP - " + otp + " please verify this otp";

        // Send the OTP message using Twilio
        Message.creator(new PhoneNumber("+91" + phoneNumber), new PhoneNumber(fromNumber), msg).create();

        // Store the OTP in the map for later verification
        otpMap.put(phoneNumber, otp);
    }

    // Method to verify an OTP
    @Override
    public void verifyOtp(String phoneNumber, Integer otp, HttpServletResponse response, HttpServletRequest request)
            throws OtpValidationException, IOException {
        if (!otpMap.containsKey(phoneNumber)) {
            throw new OtpValidationException("OTP not generated for phone number");
        } else if (Objects.equals(otpMap.get(phoneNumber), otp)) {
            generateToken(response, request, phoneNumber);
            otpMap.remove(phoneNumber);
        } else if (!Objects.equals(otpMap.get(phoneNumber), otp)) {
            throw new OtpValidationException("OTP not valid for phone number");
        }
    }

    // Method to get a user by phone number
    public User user(String phoneNumber) {
        return userServiceInterface.getUserViaPhoneNumber(phoneNumber);
    }

    // Method to generate JWT tokens and set them in the response headers
    @Override
    public void generateToken(HttpServletResponse response, HttpServletRequest request, String phoneNumber)
            throws IOException {
        // Define the path to the secret file
        String secretFilePath = "Desktop/leaps/secret/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;

        // Read the secret key from the secret file
        String secret = securityUtility.readSecretFromFile(absolutePath);
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

        // Get user information
        String role = user(phoneNumber).getRole().toString();
        String[] roles = new String[]{role};
        String email = user(phoneNumber).getEmail();
        LocalDateTime now = LocalDateTime.now();

        // Calculate access token expiration time (e.g., 1440 minutes = 1 day)
        LocalDateTime accessTokenExpirationTime = now.plusMinutes(1440);
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());

        // Calculate refresh token expiration time (e.g., 30 days)
        LocalDateTime refreshTokenExpirationTime = now.plusMinutes(43200);
        Date refreshTokenExpirationDate = Date.from(refreshTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());

        // Create an access token with claims
        String accessToken = JWT.create()
                .withSubject(email)
                .withExpiresAt(accessTokenExpirationDate)
                .withClaim("roles", Arrays.asList(roles))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        // Create a refresh token with claims
        String refreshToken = JWT.create()
                .withSubject(user(phoneNumber).getEmail())
                .withExpiresAt(refreshTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        // Set access and refresh tokens in response headers
        response.setHeader("access_token", accessToken);
        response.setHeader("refresh_token", refreshToken);

        // Save the refresh token in the database
        securityUtility.saveTokens(refreshToken, email);
    }
}
