package com.nineleaps.leaps.config.filter;

import com.nineleaps.leaps.utils.SecurityUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthorizationFilterTest {

    @Mock
    private SecurityUtility securityUtility;

    private CustomAuthorizationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new CustomAuthorizationFilter(securityUtility);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }




    @Test
    void doFilterInternal_withInvalidToken_shouldReturnForbiddenStatus() throws ServletException, IOException {
        // Arrange
        String token = "invalid_token";

        // Mock securityUtility methods
        when(securityUtility.isAccessTokenExpired(token)).thenReturn(false);

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
