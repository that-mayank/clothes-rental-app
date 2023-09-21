package com.nineleaps.leaps.utils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserLoginInfo;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.repository.UserLoginInfoRepository;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
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
import java.util.Objects;

import static com.nineleaps.leaps.LeapsApplication.ACCOUNT_LOCK_DURATION_MINUTES;
import static com.nineleaps.leaps.LeapsApplication.MAX_LOGIN_ATTEMPTS;



@Component
@AllArgsConstructor
public class SecurityUtility {

    private final UserServiceInterface userServiceInterface;
    private RefreshTokenRepository refreshTokenRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final UserRepository userRepository;

    public void getDeviceToken(String email,String deviceToken){
        userServiceInterface.saveDeviceTokenToUser(email,deviceToken);
    }

    public boolean isAccessTokenExpired(String accessToken) {
        DecodedJWT decodedAccessToken = JWT.decode(accessToken);
        Date expirationDate = decodedAccessToken.getExpiresAt();
        return expirationDate.before(new Date());
    }
    public boolean isRefreshTokenExpired(String refreshToken) {
        DecodedJWT decodedAccessToken = JWT.decode(refreshToken);
        Date expirationDate = decodedAccessToken.getExpiresAt();
        return expirationDate.before(new Date());
    }


    public boolean saveTokens(String token, String email,LocalDateTime time) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setEmail(email);
        refreshToken.setAuditColumns(time);
        refreshTokenRepository.save(refreshToken);
        return true;
    }


    public String updateAccessTokenViaRefreshToken(String email2, HttpServletRequest request,String tokenToCheck) throws IOException {

        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email2);
        String token = refreshToken.getToken();
        if(!isRefreshTokenExpired(token)){
            if(Objects.equals(token, tokenToCheck)){
                String secretFilePath = "/Desktop"+"/leaps"+"/secret"+"/secret.txt";
                String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
                String secret = readSecretFromFile(absolutePath);
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                DecodedJWT decodedRefreshToken = JWT.decode(token);
                String email = decodedRefreshToken.getSubject();
                User user = userServiceInterface.getUser(email);
                String role = user.getRole().toString();
                String[] roles = new String[]{role};
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime accessTokenExpirationTime = now.plusMinutes(2); // Update to desired expiration time
                Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
                return JWT.create()
                        .withSubject(email)
                        .withExpiresAt(accessTokenExpirationDate)
                        .withIssuer(request.getRequestURL().toString()) // Update to the appropriate issuer
                        .withClaim("roles", Arrays.asList(roles))
                        .sign(algorithm);
            }else{
                return "Invalid Refresh token";
            }
        }
        return "Refresh Token In Database Expired , Login Again !";
    }

    public String readSecretFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            return reader.readLine();
        }
    }


    public void updateLoginAttempts(String email) {
        User user = userRepository.findByEmail(email);
        Long userId = user.getId();
        UserLoginInfo loginInfo = userLoginInfoRepository.findByUserId(userId);

        if (loginInfo == null) {
            loginInfo = new UserLoginInfo();
            loginInfo.setUser(user);
            loginInfo.setLoginAttempts(1);
        } else {
            int attempts = loginInfo.getLoginAttempts();
            loginInfo.setLoginAttempts(attempts + 1);
        }

        // Check if login attempts to exceed the maximum allowed
        if (loginInfo.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
            loginInfo.lockAccount();

            // Set the lock time to the current time plus the lock duration
            LocalDateTime lockTime = LocalDateTime.now().plusMinutes(ACCOUNT_LOCK_DURATION_MINUTES);
            loginInfo.setLockTime(lockTime);
            loginInfo.setLocked(true);  // Set locked to true
        } else {
            loginInfo.setLocked(false);  // Reset locked to false if attempts < MAX_LOGIN_ATTEMPTS
        }

        // Update or save the login info
        userLoginInfoRepository.save(loginInfo);
    }


    public void checkAccountLockAndLoginAttempts(String email) {
        User user = userRepository.findByEmail(email);
        Long userId = user.getId();
        UserLoginInfo userLoginInfo = userLoginInfoRepository.findByUserId(userId);

        if(userLoginInfo == null){
            UserLoginInfo userLoginInfo2 = new UserLoginInfo();
            userLoginInfo2.initializeLoginInfo(user);
            userLoginInfoRepository.save(userLoginInfo2);
        }else{
            LocalDateTime unlockTime = userLoginInfo.getLockTime();
            if (userLoginInfo.isAccountLocked() && unlockTime != null && LocalDateTime.now().isBefore(unlockTime)) {
                throw new LockedException("Account is locked. Please try again later.");
            } else if (userLoginInfo.isAccountLocked() && unlockTime != null && LocalDateTime.now().isAfter(unlockTime)) {
                userLoginInfo.resetLoginAttempts();
                userLoginInfo.setLocked(false);  // Unlock the account
                userLoginInfo.setLockTime(null);
                userLoginInfoRepository.save(userLoginInfo);
            }
        }


    }



    public void setLastLoginAttempt(String email){
        User user = userRepository.findByEmail(email);
        Long userId = user.getId();
        UserLoginInfo userLoginInfo = userLoginInfoRepository.findByUserId(userId);
        if (userLoginInfo != null) {
            userLoginInfo.setLastLoginAttempt(LocalDateTime.now());
            userLoginInfoRepository.save(userLoginInfo);
        }
    }

    public void initializeUserLoginInfo(String email){
        User user = userRepository.findByEmail(email);
        Long userId = user.getId();
        UserLoginInfo userLoginInfo = userLoginInfoRepository.findByUserId(userId);
        if(userLoginInfo != null){
            userLoginInfo.initializeLoginInfo(user);
        }
    }




}









