package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;


@Component
@AllArgsConstructor
public class SwitchProfile {

    private final Helper helper;

    public void generateTokenForSwitchProfile(HttpServletResponse response, Role profile, HttpServletRequest request) throws IOException {
        User user = helper.getUser(request);
        String secretFilePath = "/Desktop"+"/leaps"+"/secret"+"/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
        String secret = readSecretFromFile(absolutePath);
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        String role = profile.toString();
        String[] roles = new String[]{role};
        LocalDateTime accessTokenExpirationTime = LocalDateTime.now().plusHours(24); // Update to desired expiration time
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        String accessToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(accessTokenExpirationDate)
                .withClaim("roles", Arrays.asList(roles))
                .sign(algorithm);
        response.setHeader("access_token", accessToken);
    }

    private String readSecretFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            return reader.readLine();
        }
    }
}
