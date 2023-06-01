package com.nineleaps.leaps.utils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.output.NullOutputStream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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
    public boolean saveTokens(String refresh_token,String email){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefresh_Token(refresh_token);
        refreshToken.setEmail(email);
        refreshTokenRepository.save(refreshToken);
        return true;
    }
    public String updateAccessToken(String email2, HttpServletRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email2);
        String token = refreshToken.getRefresh_Token();
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        DecodedJWT decodedRefreshToken = JWT.decode(token);
        String email = decodedRefreshToken.getSubject();
        User user = userServiceInterface.getUser(email);
        String role = user.getRole().toString();
        String[] roles = new String[]{role};
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpirationTime = now.plusMinutes(1440); // Update to desired expiration time
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        String updated_access_token = JWT.create()
                .withSubject(email)
                .withExpiresAt(accessTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString()) // Update to the appropriate issuer
                .withClaim ( "roles", Arrays.asList(roles))
                .sign(algorithm);
        return updated_access_token;
    }
//    public void autoupdateaccesstoken(HttpServletResponse response,HttpServletRequest request){
//
//        if(access_token == null){
//            System.out.println("waiting for the user to login");
//        }else{
//            if(!isAccessTokenExpired(access_token)){
//                System.out.println("token is valid and not expired");
//            }else{
//                DecodedJWT decodedAccessToken = JWT.decode(access_token);
//                String email = decodedAccessToken.getSubject();
//                RefreshToken refreshToken = refreshTokenRepository.findByEmail(email);
//                String token = refreshToken.getRefresh_Token();
//                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
//                DecodedJWT decodedRefreshToken = JWT.decode(token);
//                String Remail = decodedRefreshToken.getSubject();
//                User user = userServiceInterface.getUser(Remail);
//                String role = user.getRole().toString();
//                String[] roles = new String[]{role};
//                LocalDateTime now = LocalDateTime.now();
//                LocalDateTime accessTokenExpirationTime = now.plusMinutes(1440); // Update to desired expiration time
//                Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
//                String access_token= JWT.create()
//                        .withSubject(Remail)
//                        .withExpiresAt(accessTokenExpirationDate)
//                        .withIssuer("auto updation function")
//                        .withClaim ( "roles", Arrays.asList(roles))
//                        .sign(algorithm);
////                System.out.println(access_token);
//                response.setHeader("access_token",access_token);
//                System.out.println("token replaced with new expiry time");
//            }
//        }
//    }
//    @Scheduled(cron = "0 0 0 * * ?") // check at 1200 am everyday and replaces the token
//    public void scheduleAccessTokenReport(){
//        HttpServletResponse response = new HttpServletResponse() {
//            @Override
//            public void addCookie(Cookie cookie) {
//            }
//            @Override
//            public boolean containsHeader(String name) {
//                return false;
//            }
//            @Override
//            public String encodeURL(String url) {
//                return null;
//            }
//            @Override
//            public String encodeRedirectURL(String url) {
//                return null;
//            }
//            @Override
//            public String encodeUrl(String url) {
//                return null;
//            }
//            @Override
//            public String encodeRedirectUrl(String url) {
//                return null;
//            }
//            @Override
//            public void sendError(int sc, String msg) throws IOException {
//            }
//            @Override
//            public void sendError(int sc) throws IOException {
//            }
//            @Override
//            public void sendRedirect(String location) throws IOException {
//            }
//            @Override
//            public void setDateHeader(String name, long date) {
//            }
//            @Override
//            public void addDateHeader(String name, long date) {
//            }
//            @Override
//            public void setHeader(String name, String value) {
//            }
//            @Override
//            public void addHeader(String name, String value) {
//            }
//            @Override
//            public void setIntHeader(String name, int value) {
//            }
//            @Override
//            public void addIntHeader(String name, int value) {
//            }
//            @Override
//            public void setStatus(int sc) {
//            }
//            @Override
//            public void setStatus(int sc, String sm) {
//            }
//            @Override
//            public int getStatus() {
//                return 0;
//            }
//            @Override
//            public String getHeader(String name) {
//                return null;
//            }
//            @Override
//            public Collection<String> getHeaders(String name) {
//                return null;
//            }
//            @Override
//            public Collection<String> getHeaderNames() {
//                return null;
//            }
//            @Override
//            public String getCharacterEncoding() {
//                return null;
//            }
//            @Override
//            public String getContentType() {
//                return null;
//            }
//            @Override
//            public ServletOutputStream getOutputStream() throws IOException {
//                return null;
//            }
//            @Override
//            public PrintWriter getWriter() throws IOException {
//                return null;
//            }
//            @Override
//            public void setCharacterEncoding(String charset) {
//            }
//            @Override
//            public void setContentLength(int len) {
//            }
//            @Override
//            public void setContentLengthLong(long length) {
//            }
//            @Override
//            public void setContentType(String type) {
//            }
//            @Override
//            public void setBufferSize(int size) {
//            }
//            @Override
//            public int getBufferSize() {
//                return 0;
//            }
//            @Override
//            public void flushBuffer() throws IOException {
//            }
//            @Override
//            public void resetBuffer() {
//            }
//            @Override
//            public boolean isCommitted() {
//                return false;
//            }
//            @Override
//            public void reset() {
//            }
//            @Override
//            public void setLocale(Locale loc) {
//            }
//            @Override
//            public Locale getLocale() {
//                return null;
//            }
//        };
//        autoupdateaccesstoken(response);
//    }
}









