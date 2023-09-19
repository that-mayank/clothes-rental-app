package com.nineleaps.leaps.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.repository.UserLoginInfoRepository;
import com.nineleaps.leaps.utils.SecurityUtility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    // Linking layers using Constructor Injection
    private final AuthenticationManager authenticationManager;
    private final SecurityUtility securityUtility;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;


    // API - Authenticates the user using login credentials - email and password
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // Fetch the email and Password Params
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Fetch DeviceToken from Params
       String deviceToken = request.getParameter("deviceToken");

       // Check if the User's Account is Locked or Not
        securityUtility.checkAccountLockAndLoginAttempts(email);

        // Generate UsernamePasswordAuthenticationToken for the Right Credentials
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        // Set device token if available
        securityUtility.getDeviceToken(email, Objects.requireNonNullElse(deviceToken, ""));
        return authenticationManager.authenticate(authenticationToken);

    }

    // API - Generate Access-Token and Refresh-Token On Successful Authentication and set the Tokens to the Headers
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // Fetch User from Spring-Security UserDetails
        User user = (User) authentication.getPrincipal();

        // Extract Secret Key from the Secret File Path for Generating Token
        String secretFilePath = "/Desktop"+"/leaps"+"/secret"+"/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
        String secret = securityUtility.readSecretFromFile(absolutePath);

        // Set the Algorithm
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

        // Fetch the local time and set the Access Token Expiry Time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpirationTime = now.plusMinutes(1); // Update to desired expiration time 24hrs
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());

        // Generate Token
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(accessTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString()) // Requested Source URL
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())) // Set Roles
                .sign(algorithm);

        // Fetch the Local Time and set the Refresh Token Expiry Time
        LocalDateTime refreshTokenExpirationTime = now.plusMinutes(43200); // Update to desired expiration time 30 days
        Date refreshTokenExpirationDate = Date.from(refreshTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());

        // Generate Token
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(refreshTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString()) // Requested Source URL
                .sign(algorithm);

        // Fetch Username which is generally the Email of the User
        String email = user.getUsername();

        // Set the Time of Last Successful Login Attempt
        securityUtility.setLastLoginAttempt(email);

        // Initialize the UserLoginInfo Table for the Successfully Logged-In User
        securityUtility.initializeUserLoginInfo(email);

        // Save Refresh Token to the Database
        if (securityUtility.saveTokens(refreshToken, email)) {
            response.getWriter().write("RefreshTokens added successfully!");
        } else {
            response.getWriter().write("Token not added");
        }

        // Set the Generated Tokens to their Respective Headers
        response.setHeader("access_token", accessToken);
        response.setHeader("refresh_token", refreshToken);
    }

    // API - Handles Unsuccessful Authentication
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        try {
            super.unsuccessfulAuthentication(request, response, failed);

            // Fetch Email From the Request Parameter
            String email = request.getParameter("email");

            // Update login attempts and check for account lockout . Interacts with Security Utility
            securityUtility.updateLoginAttempts(email);

            // Set HTTP status to UNAUTHORIZED (401) since authentication was unsuccessful
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + failed.getMessage());
        } catch (IOException | ServletException e) {

            // Handle any IO or servlet-related exceptions
            // set  specific HTTP status for these exceptions
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during unsuccessful authentication");
            // log the exception
            logger.error("An error occurred during unsuccessful authentication", e);
        }
    }



}