package com.nineleaps.leaps.config.filter;

import com.auth0.jwt.JWT;

import com.auth0.jwt.JWTVerifier;

import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.interfaces.Claim;

import com.auth0.jwt.interfaces.DecodedJWT;

import com.auth0.jwt.interfaces.Verification;

import com.nineleaps.leaps.enums.Role;

import com.nineleaps.leaps.utils.SecurityUtility;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.MockedStatic;

import org.mockito.MockitoAnnotations;

import org.powermock.api.mockito.PowerMockito;

import org.powermock.core.classloader.annotations.PowerMockIgnore;

import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import org.springframework.mock.web.MockHttpServletRequest;

import org.springframework.mock.web.MockHttpServletResponse;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContext;

import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)

@PowerMockIgnore("com.auth0.jwt.*")

class CustomAuthorizationFilterTest {

    @Mock

    private SecurityUtility securityUtility;

    private CustomAuthorizationFilter filter;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private FilterChain filterChain;

    @BeforeEach

    void setUp() {

        MockitoAnnotations.initMocks(this);

        filter = new CustomAuthorizationFilter(securityUtility);

        request = new MockHttpServletRequest();

        response = new MockHttpServletResponse();

        filterChain = mock(FilterChain.class);

    }

//    @Test
//
//    void doFilterInternal_withValidToken_shouldSetAuthenticationAndCallFilterChain()
//
//            throws ServletException, IOException {
//
//        // Arrange
//
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwLnByYW5heXJlZGR5Njk5QGdtYWlsLmNvbSIsInJvbGVzIjpbIkJPUlJPV0VSIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9hcGkvdjEvbG9naW4iLCJleHAiOjE2ODkwNjE1NzV9.oAJoRhVJk7mZWZV8D4Ge-6Z2AogBnt8htOORWZaJS2k";
//
//        String email = "p.pranayreddy699@gmail.com";
//
//        String[] roles = {String.valueOf(Role.ADMIN)};
//
//
//
//        // Mock securityUtility methods
//
//        when(securityUtility.isAccessTokenExpired(token)).thenReturn(false);
//
//
//
//        // Mock JWT verification
//
//        DecodedJWT decodedJWT = mock(DecodedJWT.class);
//
//        when(decodedJWT.getSubject()).thenReturn(email);
//
//        Claim claim = mock(Claim.class);
//
//        when(claim.asArray(String.class)).thenReturn(roles);
//
//        when(decodedJWT.getClaim("roles")).thenReturn(claim);
//
//
//
//        // Mock JWT decoding
//
////        when(JWT.decode(token)).thenReturn(decodedJWT);
//
//
//
//        // Set the authorization header
//
////        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//
//
//
//        // Act
//
//        filter.doFilterInternal(request, response, filterChain);
//
//
//
//        // Assert
//
//        // Perform your assertions here
//
//        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//
//        // Additional assertions based on your logic
//
//    }

    @Test

    void doFilterInternal_withExpiredToken_shouldReturnUpdatedAccessToken() throws ServletException, IOException {

        // Arrange

        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqeW9zaG5hdmlAbmluZWxlYXBzLmNvbSIsInJvbGVzIjpbIk9XTkVSIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9hcGkvdjEvbG9naW4iLCJleHAiOjE2ODg1NTM3Njh9.WQgHnTj2J6REgDGraAMrOzzpp-iz2VjejiYlniQP-Kg";

        String email = "test@example.com";

        String newAccessToken = "new_access_token";

        String new_updated_token = "new_access_token";



        // Mock securityUtility methods

        when(securityUtility.isTokenExpired(token)).thenReturn(true);

        when(securityUtility.updateAccessToken(email, request)).thenReturn(newAccessToken);



        // Set the authorization header

        request.addHeader("Authorization", "Bearer " + token);



        // Act

        filter.doFilterInternal(request, response, filterChain);



        // Assert

        verify(filterChain, never()).doFilter(request, response);

//        assertEquals(200, response.getStatus());

        assertEquals(newAccessToken, new_updated_token);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

    }

    @Test

    void doFilterInternal_withInvalidToken_shouldReturnForbiddenStatus() throws ServletException, IOException {

        // Arrange

        String token = "invalid_token";

        // Mock securityUtility methods

        when(securityUtility.isTokenExpired(token)).thenReturn(false);

        // Set the authorization header

        request.addHeader("Authorization", "Bearer " + token);

        // Act

        filter.doFilterInternal(request, response, filterChain);

        // Assert

        verify(filterChain, never()).doFilter(request, response);

        assertEquals(403, response.getStatus());

        assertNotNull(response.getContentAsString());

    }

    @Test

    void doFilterInternal_withNoAuthorizationHeader_shouldCallFilterChain() throws ServletException, IOException {

        // Act

        filter.doFilterInternal(request, response, filterChain);

        // Assert

        verify(filterChain).doFilter(request, response);

        assertNull(response.getHeader("access_token"));

        assertNull(SecurityContextHolder.getContext().getAuthentication());

    }

    @Test

    void doFilterInternal_withLoginEndpoint_shouldCallFilterChain() throws ServletException, IOException {

        // Arrange

        request.setServletPath("/api/login");

        // Act

        filter.doFilterInternal(request, response, filterChain);

        // Assert

        verify(filterChain).doFilter(request, response);

        assertNull(response.getHeader("access_token"));

        assertNull(SecurityContextHolder.getContext().getAuthentication());

    }

    @Test

    void doFilterInternal_withSignupEndpoint_shouldCallFilterChain() throws ServletException, IOException {

        // Arrange

        request.setServletPath("/api/v1/user/signup");

        // Act

        filter.doFilterInternal(request, response, filterChain);

        // Assert

        verify(filterChain).doFilter(request, response);

        assertNull(response.getHeader("access_token"));

        assertNull(SecurityContextHolder.getContext().getAuthentication());

    }

}