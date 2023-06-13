package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class SecurityUtility {
    private final UserServiceInterface userServiceInterface;
    private final RefreshTokenRepository refreshTokenRepository;

    public boolean isAccessTokenExpired(String accessToken) {
        DecodedJWT decodedAccessToken = JWT.decode(accessToken);
        Date expirationDate = decodedAccessToken.getExpiresAt();
        return expirationDate.before(new Date());
    }

    public boolean saveTokens(String refresh_token, String email) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refresh_token);
        refreshToken.setEmail(email);
        refreshTokenRepository.save(refreshToken);
        return true;
    }

    public String updateAccessToken(String email2, HttpServletRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email2);
        String token = refreshToken.getToken();
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        DecodedJWT decodedRefreshToken = JWT.decode(token);
        String email = decodedRefreshToken.getSubject();
        User user = userServiceInterface.getUser(email);
        String role = user.getRole().toString();
        String[] roles = new String[]{role};
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpirationTime = now.plusMinutes(1440); // Update to desired expiration time
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(accessTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString()) // Update to the appropriate issuer
                .withClaim("roles", Arrays.asList(roles))
                .sign(algorithm);

    }
}









