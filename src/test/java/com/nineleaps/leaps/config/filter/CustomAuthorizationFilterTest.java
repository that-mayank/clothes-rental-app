package com.nineleaps.leaps.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.utils.SecurityUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


class CustomAuthorizationFilterTest {

    @InjectMocks
    private CustomAuthorizationFilter authorizationFilter;

    @Mock
    private SecurityUtility securityUtility;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getServletPath()).thenReturn("/api/v1/secure");

        when(request.getHeader("Authorization")).thenReturn(null);

        authorizationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ExemptedURL() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer ValidToken");
        when(request.getServletPath()).thenReturn("/api/v1/login");

        authorizationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }


    @Test
    void testDoFilterInternal_HandleRefreshToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer RefreshToken");
        when(request.getServletPath()).thenReturn("/api/v1/user/refreshToken");
        when(securityUtility.isTokenExpired("RefreshToken")).thenReturn(false);

        authorizationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testHandleRefreshTokenWhenAuthorizationHeaderIsPresentAndTokenIsNotExpired() throws Exception {
        // Define the authorization header with a valid token
        when(request.getHeader(AUTHORIZATION)).thenReturn( "Bearer valid-token");

        // Mock the behavior of securityUtility to indicate a valid token
        when(securityUtility.isTokenExpired("valid-token")).thenReturn(false);

        // Call the method to test
        authorizationFilter.handleRefreshToken(request, response, filterChain);

        // Assert that filterChain.doFilter is called
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testHandleAccessTokenValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Mock a valid JWT token
        String validToken = generateAccessToken(60);
        request.addHeader("Authorization", "Bearer " + validToken);

        // Mock UserDetails and UserDetailsService
        UserDetails userDetails = new User(
                "admin@nineleaps.com", "Admin", Collections.emptyList()
        );
        UserDetailsService userDetailsService = username -> userDetails;

        // Set up SecurityContext with a valid user
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetailsService, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(securityUtility.readSecretFromFile(anyString())).thenReturn("meinhuchotadon");


        // Call the handleAccessToken method
        authorizationFilter.handleAccessToken(validToken, response, filterChain, request);

        // Assert that the response is not an error (i.e., HTTP status code 200)
        // You can check the response content or other details as well
         assertEquals(200, response.getStatus());
    }

    private String generateAccessToken(int expirationMinutes) {
        Algorithm algorithm = Algorithm.HMAC256("meinhuchotadon");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(expirationMinutes);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
        return JWT.create()
                .withSubject("admin@nineleaps.com")
                .withExpiresAt(expirationDate)
                .withIssuer("https://example.com")
                .withClaim("roles", Arrays.asList("ADMIN"))
                .sign(algorithm);
    }
}
