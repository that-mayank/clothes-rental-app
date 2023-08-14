package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserDeviceDetail;
import com.nineleaps.leaps.model.tokens.AccessToken;
import com.nineleaps.leaps.model.tokens.RefreshToken;
import com.nineleaps.leaps.repository.AccessTokenRepository;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.repository.UserDeviceDetailRepository;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class SecurityUtility {
    private final UserServiceInterface userServiceInterface;

    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserDeviceDetailRepository userDeviceDetailRepository;





    public String readSecretFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            return reader.readLine();
        }
    }

    public boolean saveAccessToken(String email, String accessToken, Date accessTokenExpirationDate, String uniqueDeviceId) {
        User user = userRepository.findByEmail(email);
        UserDeviceDetail userDeviceDetail = userDeviceDetailRepository.findByUserAndUniqueDeviceId(user, uniqueDeviceId);

        if (userDeviceDetail != null) {
            // UserDeviceDetail with uniqueDeviceId already exists, update tokens
            AccessToken existingAccessToken = userDeviceDetail.getAccessToken();
            if (existingAccessToken != null) {
                existingAccessToken.setJwtToken(accessToken);
                existingAccessToken.setTokenExpiry(accessTokenExpirationDate);
                existingAccessToken.setExpired(false);
                existingAccessToken.setRevoked(false);
                accessTokenRepository.save(existingAccessToken);
            } else {
                // Create a new AccessToken entity
                AccessToken accessTokenEntity = new AccessToken();
                accessTokenEntity.setUser(user);
                accessTokenEntity.setJwtToken(accessToken);
                accessTokenEntity.setTokenExpiry(accessTokenExpirationDate);
                accessTokenEntity.setExpired(false);
                accessTokenEntity.setRevoked(false);
                accessTokenEntity.setUserDeviceDetail(userDeviceDetail);
                accessTokenRepository.save(accessTokenEntity);
            }
        } else {
            // UserDeviceDetail with uniqueDeviceId does not exist, create new
            userDeviceDetail = new UserDeviceDetail();
            userDeviceDetail.setUser(user);
            userDeviceDetail.setUniqueDeviceId(uniqueDeviceId);
            userDeviceDetailRepository.save(userDeviceDetail);

            // Create a new AccessToken entity
            AccessToken accessTokenEntity = new AccessToken();
            accessTokenEntity.setUser(user);
            accessTokenEntity.setJwtToken(accessToken);
            accessTokenEntity.setTokenExpiry(accessTokenExpirationDate);
            accessTokenEntity.setExpired(false);
            accessTokenEntity.setRevoked(false);
            accessTokenEntity.setUserDeviceDetail(userDeviceDetail);
            accessTokenRepository.save(accessTokenEntity);
        }

        return true; // Return true if the token was saved or updated successfully
    }

    public boolean saveRefreshToken(String email, String refreshToken, Date refreshTokenExpirationDate, String uniqueDeviceId) {
        User user = userRepository.findByEmail(email);
        UserDeviceDetail userDeviceDetail = userDeviceDetailRepository.findByUserAndUniqueDeviceId(user, uniqueDeviceId);

        if (userDeviceDetail != null) {
            // UserDeviceDetail with uniqueDeviceId already exists, update tokens
            RefreshToken existingRefreshToken = userDeviceDetail.getRefreshToken();
            if (existingRefreshToken != null) {
                existingRefreshToken.setJwtToken(refreshToken);
                existingRefreshToken.setTokenExpiry(refreshTokenExpirationDate);
                existingRefreshToken.setExpired(false);
                existingRefreshToken.setRevoked(false);
                refreshTokenRepository.save(existingRefreshToken);
            } else {
                // Create a new RefreshToken entity
                RefreshToken refreshTokenEntity = new RefreshToken();
                refreshTokenEntity.setUser(user);
                refreshTokenEntity.setJwtToken(refreshToken);
                refreshTokenEntity.setTokenExpiry(refreshTokenExpirationDate);
                refreshTokenEntity.setExpired(false);
                refreshTokenEntity.setRevoked(false);
                refreshTokenEntity.setUserDeviceDetail(userDeviceDetail);
                refreshTokenRepository.save(refreshTokenEntity);
            }
        } else {
            // UserDeviceDetail with uniqueDeviceId does not exist, create new
            userDeviceDetail = new UserDeviceDetail();
            userDeviceDetail.setUser(user);
            userDeviceDetail.setUniqueDeviceId(uniqueDeviceId);
            userDeviceDetailRepository.save(userDeviceDetail);

            // Create a new RefreshToken entity
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setUser(user);
            refreshTokenEntity.setJwtToken(refreshToken);
            refreshTokenEntity.setTokenExpiry(refreshTokenExpirationDate);
            refreshTokenEntity.setExpired(false);
            refreshTokenEntity.setRevoked(false);
            refreshTokenEntity.setUserDeviceDetail(userDeviceDetail);
            refreshTokenRepository.save(refreshTokenEntity);
        }

        return true; // Return true if the token was saved or updated successfully
    }


    public boolean isAccesstokenValid(String token){
        Boolean expiry = false;
        try{

            DecodedJWT decodedJWT = JWT.decode(token);
            String email = decodedJWT.getSubject();

            //checks if the token stored for the particular database is valid
            AccessToken accessToken = accessTokenRepository.findByJwtTokenAndUser_Email(token,email);
            if(accessToken!=null && !accessToken.isExpired() && !accessToken.isRevoked() && accessToken.getTokenExpiry().after(new Date())){
                expiry = true;
            }
            return expiry;
        }catch(Exception e){
            return expiry;
        }
    }

    public String updateExpiredAccessTokenViaRefreshToken(HttpServletRequest request, HttpServletResponse response, String deviceUniqueId, String refreshToken) {
        try {
            DecodedJWT decodedJWT = JWT.decode(refreshToken);

            if (decodedJWT.getExpiresAt().after(new Date())) {
                String userEmail = decodedJWT.getSubject();
                User user = userRepository.findByEmail(userEmail);

                if (user != null) {
                    String role = user.getRole().toString();
                    String[] roles = new String[]{role};

                    RefreshToken existingRefreshToken = refreshTokenRepository.findByJwtTokenAndUser_EmailAndUserDeviceDetail_UniqueDeviceId(refreshToken, userEmail, deviceUniqueId);

                    if (existingRefreshToken != null && !existingRefreshToken.isExpired() && existingRefreshToken.getTokenExpiry().after(new Date())) {
                        String secretFilePath = "Desktop/Leaps-Backend/secret/secret.txt"; // Update this path
                        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
                        String secret = readSecretFromFile(absolutePath); // Implement readSecretFromFile method
                        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

                        AccessToken existingAccessToken = accessTokenRepository.findByUser_EmailAndUserDeviceDetail_UniqueDeviceId(user.getEmail(),deviceUniqueId);

                        // Generate a new access token with a new expiration time
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime accessTokenExpirationTime = now.plusMinutes(2); // Update to desired expiration time
                        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());

                        String newAccessToken = JWT.create()
                                .withSubject(userEmail)
                                .withExpiresAt(accessTokenExpirationDate)
                                .withIssuer(request.getRequestURL().toString())
                                .withClaim("roles", Arrays.asList(roles))
                                .sign(algorithm);

                        // Update the access token in the database
                        existingAccessToken.setJwtToken(newAccessToken);
                        existingAccessToken.setTokenExpiry(accessTokenExpirationDate);
                        accessTokenRepository.save(existingAccessToken);

                        return newAccessToken;

                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED, "DB-RefreshToken token expired");
                        throw new AuthenticationFailException("DB-RefreshToken Expired");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                    throw new UsernameNotFoundException("User not found");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED, "RefreshToken token sent via header got expired");
                throw new AuthenticationFailException("Header-RefreshToken Expired");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED, "RefreshToken verification failed");
            throw new AuthenticationFailException("RefreshToken Verification Failed");
        }
    }

}









