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

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Getter
@Setter
@AllArgsConstructor
@Component
public class Helper {

    private final UserRepository userRepository;

    public User getUser(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        DecodedJWT decodedAccessToken = JWT.decode(token);
        String email = decodedAccessToken.getSubject();
        return userRepository.findByEmail(email);
    }


}
