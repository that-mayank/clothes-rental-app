package com.nineleaps.leaps.config.filter;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.utils.SecurityUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


class CustomAuthorizationFilterTest {

    @InjectMocks
    private CustomAuthorizationFilter authorizationFilter;

    @Mock
    private SecurityUtility securityUtility;

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
        when(securityUtility.isTokenExpired("RefreshToken")).thenReturn(true);

        authorizationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_RefreshTokenExpired() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer RefreshToken");
        when(request.getServletPath()).thenReturn("/api/v1/user/refreshToken");
        when(securityUtility.isTokenExpired("RefreshToken")).thenReturn(true);

        authorizationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_RefreshTokenValid() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer RefreshToken");
        when(request.getServletPath()).thenReturn("/api/v1/user/refreshToken");
        when(securityUtility.isTokenExpired("RefreshToken")).thenReturn(false);

        authorizationFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token expired");
    }

}
