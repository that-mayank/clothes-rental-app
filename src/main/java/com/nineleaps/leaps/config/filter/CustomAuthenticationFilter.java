package com.nineleaps.leaps.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nineleaps.leaps.exceptions.RuntimeCustomException;
import com.nineleaps.leaps.utils.SecurityUtility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final SecurityUtility securityUtility;

    //Authenticates the user using login credentials - email and password
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(request.getInputStream());
        } catch (IOException e) {
            throw new RuntimeCustomException("Error occurred while reading JSON data from the request.");

        }
        // Extract the username and password from the JSON data
        String email = jsonNode.get("email").asText();
        String password = jsonNode.get("password").asText();
        // Check if device token is present
        Optional<JsonNode> deviceTokenNodeOptional = Optional.ofNullable(jsonNode.get("deviceToken"));
        String deviceToken = deviceTokenNodeOptional.map(JsonNode::asText).orElse(null);
        // Process authentication
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        // Set device token if available
        Optional.ofNullable(deviceToken).ifPresent(token -> securityUtility.setDeviceToken(email, token));
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        String secretFilePath = "/Desktop"+"/leaps"+"/secret"+"/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
        String secret = securityUtility.readSecretFromFile(absolutePath);
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        // Update access token expiration time dynamically
        LocalDateTime accessTokenExpirationTime = LocalDateTime.now().plusHours(24); // Update to desired expiration time 24hrs or one day
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(accessTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        // Update refresh token expiration time dynamically
        LocalDateTime refreshTokenExpirationTime = LocalDateTime.now().plusDays(30); // Update to desired expiration time 30 days
        Date refreshTokenExpirationDate = Date.from(refreshTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(refreshTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        String email = user.getUsername();

        Optional<String> result = Optional.of(refreshToken)
                .map(token -> securityUtility.saveTokens(token, email) ? "RefreshTokens added successfully!" : "Token not added");
        result.ifPresent(response.getWriter()::write);

        response.setHeader("access_token", accessToken);
        response.setHeader("refresh_token", refreshToken);
    }
}