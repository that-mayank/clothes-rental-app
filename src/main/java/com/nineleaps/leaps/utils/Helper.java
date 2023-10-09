package com.nineleaps.leaps.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Getter
@Setter
@AllArgsConstructor
@Component
public class Helper {

    private final UserRepository userRepository;

    public static boolean notNull(Object obj) {
        return obj != null;
    }

    public User getUser(String token) {
        DecodedJWT decodedAccessToken = JWT.decode(token);
        String email = decodedAccessToken.getSubject();
        return userRepository.findByEmail(email);
    }

    public User getUserFromToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        return getUser(token);
    }

    public int generateOtp() {
        int min = 100000;
        int max = 999999;
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(max - min + 1) + min;
    }



}
