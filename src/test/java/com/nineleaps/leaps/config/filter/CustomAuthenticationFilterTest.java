package com.nineleaps.leaps.config.filter;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.slf4j.Logger;
import org.springframework.security.core.userdetails.User;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.repository.UserLoginInfoRepository;
import com.nineleaps.leaps.utils.SecurityUtility;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@Tag("unit_tests")
@DisplayName("CustomAuthenticationFilter Test class")
class CustomAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityUtility securityUtility;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserLoginInfoRepository userLoginInfoRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private FilterChain chain;
    @Mock
    private AuthenticationException authenticationException;

    @Mock
    private PrintWriter writer;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Logger logger;
    @InjectMocks
    private CustomAuthenticationFilter authenticationFilter;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }
@DisplayName("Constructor Injection Test")
    @Test
    void constructorInjection() {
        // Create a CustomAuthenticationFilter instance
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
                authenticationManager,
                securityUtility,
                refreshTokenRepository,
                userLoginInfoRepository
        );

        // Verify that the dependencies were correctly injected
        Assertions.assertSame(authenticationManager, customAuthenticationFilter.getAuthenticationManager());
        Assertions.assertSame(securityUtility, customAuthenticationFilter.getSecurityUtility());
        Assertions.assertSame(refreshTokenRepository, customAuthenticationFilter.getRefreshTokenRepository());
        Assertions.assertSame(userLoginInfoRepository, customAuthenticationFilter.getUserLoginInfoRepository());
    }
    @DisplayName("Attempt Authentication Test")
    @Test
    void attemptAuthentication_Success() throws AuthenticationException {
        String email = "test@example.com";
        String password = "password";
        String deviceToken = "deviceToken";

        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);
        when(request.getParameter("deviceToken")).thenReturn(deviceToken);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mock(Authentication.class));

        Authentication result = authenticationFilter.attemptAuthentication(request, response);

        verify(request, times(1)).getParameter("email");
        verify(request, times(1)).getParameter("password");
        verify(request, times(1)).getParameter("deviceToken");
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
    }
    @DisplayName("Edge Case of Attempt Authentication Test")
    @Test
    void attemptAuthentication_UsernameNotFoundException() throws AuthenticationException {
        String email = "test@example.com";
        String password = "password";
        String deviceToken = "deviceToken";

        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);
        when(request.getParameter("deviceToken")).thenReturn(deviceToken);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // Execute the test
        AuthenticationException exception = org.junit.jupiter.api.Assertions.assertThrows(
                AuthenticationException.class,
                () -> authenticationFilter.attemptAuthentication(request, response)
        );

        // Verify
        verify(request, times(1)).getParameter("email");
        verify(request, times(1)).getParameter("password");
        verify(request, times(1)).getParameter("deviceToken");
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        org.junit.jupiter.api.Assertions.assertEquals("User not found", exception.getMessage());
    }
    @DisplayName("SuccessfulAuthentication Test")
    @Test
    void successfulAuthentication() throws IOException {
        // Create a user
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        User user = new User("test@example.com", "password", authorities);

        // Create an authentication token
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

        // Set up the request URL
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://example.com"));

        // Set up the response
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        // Mock the secret
        String secret = "testSecret";  // Replace with your actual secret
        when(securityUtility.readSecretFromFile(anyString())).thenReturn(secret);

        // Mock the saveTokens to return true
        when(securityUtility.saveTokens(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(true);

        // Call the successfulAuthentication method
        authenticationFilter.successfulAuthentication(request, response, chain, authentication);

        // Verify that the tokens are added to the headers
        verify(response).setHeader(eq("access_token"), anyString());
        verify(response).setHeader(eq("refresh_token"), anyString());


        // Verify that setLastLoginAttempt and initializeUserLoginInfo were called with the correct email
        verify(securityUtility).setLastLoginAttempt(anyString());
        verify(securityUtility).initializeUserLoginInfo(anyString());
    }

    @DisplayName("UnSuccessfulAuthentication Test")
    @Test
    void unsuccessfulAuthentication() throws IOException, ServletException {


        String email = "test@example.com";

        // Mock the request to return the email
        when(request.getParameter("email")).thenReturn(email);

        // Mock the response writer
        when(response.getWriter()).thenReturn(writer);

        // Mock the logger
        when(logger.isErrorEnabled()).thenReturn(true);

        // Call the unsuccessfulAuthentication method
        authenticationFilter.unsuccessfulAuthentication(request, response, authenticationException);

        // Verify that the email parameter was retrieved
        verify(request).getParameter("email");

        // Verify that updateLoginAttempts was called with the correct email
        verify(securityUtility).updateLoginAttempts(email);

        // Verify that sendError was called with the correct HTTP status and message
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + authenticationException.getMessage());

        // Verify that the response writer was not called (since an error was sent)
        verify(writer, never()).write(anyString());


    }
    @DisplayName("UnSuccessfulAuthentication Test for IOException Handling")
    @Test
    void unsuccessfulAuthenticationIOExceptionHandling() throws IOException, ServletException {
        String email = "test@example.com";

        // Mock the request to return the email
        when(request.getParameter("email")).thenReturn("test@example.com");

        // Mock the response writer to throw an IOException
        when(response.getWriter()).thenThrow(new IOException());

        // Call the unsuccessfulAuthentication method
        authenticationFilter.unsuccessfulAuthentication(request, response, authenticationException);

        // Verify that sendError was called with the correct HTTP status and message
        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + authenticationException.getMessage());
    }

}
