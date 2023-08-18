package com.nineleaps.leaps.config.filter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nineleaps.leaps.utils.SecurityUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final SecurityUtility securityUtility;
    String bearerHeader = "Bearer ";

    public CustomAuthorizationFilter(SecurityUtility securityUtility) {
        this.securityUtility = securityUtility;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String servletPath = request.getServletPath();

        // List of API URLs that do not require token
        List<String> exemptedUrls = Arrays.asList("/api/v1/login", "/api/v1/user/signup", "/api/v1/public");

        if (exemptedUrls.contains(servletPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (servletPath.equals("/api/v1/user/refreshToken") || servletPath.equals("/api/v1/user/logout")) {
            handleRefreshToken(request, response, filterChain);
            return;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith(bearerHeader)) {
            try {
                String token = authorizationHeader.substring(bearerHeader.length());

                // Check if the requested path starts with "/api/v1/"
                if (servletPath.startsWith("/api/v1/")) {
                    handleAccessToken(token, response, filterChain, request);
                } else {
                    handleUnauthorized(response, "Unauthorized Request");
                }
            } catch (Exception exception) {
                handleUnauthorized(response, exception.getMessage());
            }
        } else {
            // No authorization header, allow the request to proceed
            filterChain.doFilter(request, response);
        }
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null) {
            try {
                String token = authorizationHeader.substring(bearerHeader.length());
                if (!securityUtility.isRefreshTokenExpired(token)) {
                    filterChain.doFilter(request, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED, "Refreshtoken token expired");
                }
            } catch (Exception e) {
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("Error with Header token", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
    }

    private void handleAccessToken(String token, HttpServletResponse response, FilterChain filterChain, HttpServletRequest request) throws IOException, ServletException {
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
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED, "Access Token token expired");

        }

    }

    private void handleUnauthorized(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", errorMessage);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}