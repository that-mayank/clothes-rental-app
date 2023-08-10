package com.nineleaps.leaps.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

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


}
