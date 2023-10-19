package com.nineleaps.leaps.config.filter;

import com.nineleaps.leaps.exceptions.RuntimeCustomException;
import com.nineleaps.leaps.utils.SecurityUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SecurityUtility securityUtility;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @InjectMocks
    private CustomAuthenticationFilter customAuthenticationFilter;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void attemptAuthentication_ValidCredentials_ReturnsAuthentication() throws Exception {
        // Arrange
        String email = "jyoshnavi@nineleaps.com";
        String password = "jyosh@123";
        String jsonInput = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonInput.getBytes());
        when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(inputStream));
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        // Act
        Authentication result = customAuthenticationFilter.attemptAuthentication(request, response);
        // Assert
        assertEquals(authentication, result);
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void attemptAuthentication_InvalidJsonData_ThrowsRuntimeCustomException() throws Exception {
        // Arrange
        String invalidJsonInput = "invalid json data";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidJsonInput.getBytes());
        when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(inputStream));
        // Act & Assert
        assertThrows(RuntimeCustomException.class, () -> customAuthenticationFilter.attemptAuthentication(request, response));
    }

    @Test
    void successfulAuthentication_GeneratesTokensAndSetsResponseHeaders() throws Exception {
        // Arrange
        User user = new User("jyoshnavi@nineleaps.com", "jyosh@123", getAuthorities());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        String secretFilePath = "/Desktop"+"/leaps"+"/secret"+"/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
        // Stub the getRequestURL() method
        when(request.getRequestURL()).thenReturn(new StringBuffer(NGROK));
        // Mock the successful saving of tokens
        when(securityUtility.saveTokens(anyString(), anyString())).thenReturn(true);
        when(securityUtility.readSecretFromFile(absolutePath)).thenReturn("meinhuchotadon");
        // Act
        customAuthenticationFilter.successfulAuthentication(request, response, filterChain, authentication);
        // Assert
        verify(response).setHeader(eq("access_token"), argThat(matchesAccessToken()));
        verify(response).setHeader(eq("refresh_token"), argThat(matchesRefreshToken()));
        verify(writer).write("RefreshTokens added successfully!");
        verify(securityUtility).saveTokens(anyString(), eq(user.getUsername()));
    }

    private ArgumentMatcher<String> matchesAccessToken() {
        return accessToken -> {
            // Implement the logic to match the access token format or criteria
            String regex = "^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$";
            return Pattern.matches(regex, accessToken);
        };
    }

    private ArgumentMatcher<String> matchesRefreshToken() {
        return refreshToken -> {
            // Implement the logic to match the refresh token format or criteria
            String regex = "^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$";
            return Pattern.matches(regex, refreshToken);
        };
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN")); // Replace with actual roles
        // Add more authorities if needed
        return authorities;
    }
}