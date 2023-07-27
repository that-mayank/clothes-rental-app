package com.nineleaps.leaps.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nineleaps.leaps.utils.SecurityUtility;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final SecurityUtility securityUtility;

    public CustomAuthorizationFilter(SecurityUtility securityUtility) {


        this.securityUtility = securityUtility;

    }

    // method for authorizing api requests to respective person
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/v1/user/signup") || request.getServletPath().equals("/api/v1/user/refreshToken")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    if (!securityUtility.isAccessTokenExpired(token)) {
                        String secretFilePath = "Desktop/leaps/secret/secret.txt";
                        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
                        String secret = securityUtility.readSecretFromFile(absolutePath);
                        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                        JWTVerifier verifier = JWT.require(algorithm).build();
                        DecodedJWT decodedJWT = verifier.verify(token);
                        String email = decodedJWT.getSubject();
                        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(email, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                        filterChain.doFilter(request, response);
                    } else {
//                        DecodedJWT decodedAccessToken = JWT.decode(token);
//                        String email = decodedAccessToken.getSubject();
//                        String accessToken = securityUtility.updateAccessToken(email, request);
//                        response.setHeader("access_token", accessToken);

                        response.setStatus(UNAUTHORIZED.value(),"Access Token Expired");
                    }
                } catch (Exception exception) {
//

                    response.setStatus(FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", exception.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }


}