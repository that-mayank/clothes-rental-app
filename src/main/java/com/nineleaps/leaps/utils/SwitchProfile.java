package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Component
@AllArgsConstructor
public class SwitchProfile {

    private final Helper helper;

    public void generateTokenForSwitchProfile(HttpServletResponse response, Role profile, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String role = profile.toString();
        String[] roles = new String[]{role};
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpirationTime = now.plusMinutes(1440); // Update to desired expiration time
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        String accessToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(accessTokenExpirationDate)
                .withClaim("roles", Arrays.asList(roles))
                .sign(algorithm);
        response.setHeader("access_token", accessToken);
    }
}
