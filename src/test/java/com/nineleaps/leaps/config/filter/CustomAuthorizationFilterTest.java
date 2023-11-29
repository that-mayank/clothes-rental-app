package com.nineleaps.leaps.config.filter;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nineleaps.leaps.RuntimeBenchmarkExtension;
import com.nineleaps.leaps.utils.SecurityUtility;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletResponse;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.mockito.Mockito.*;

@Tag("Custom Authorization Filter")
@DisplayName("Test cases for Custom Authorization filter")
@ExtendWith(RuntimeBenchmarkExtension.class)
class CustomAuthorizationFilterTest {

    @Mock
    private SecurityUtility securityUtility;

    @InjectMocks
    private CustomAuthorizationFilter customAuthorizationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    @DisplayName("test case for login url")
    void doFilterInternal_ExemptedUrls() throws ServletException, IOException {
        // Setup: Mock the HttpServletRequest and HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(request.getServletPath()).thenReturn("/api/v1/login");

        // Call the doFilterInternal method
        customAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Verify that filterChain.doFilter is called
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("test case for refresh token url")
    void doFilterInternal_RefreshTokenUrl() throws ServletException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Setup: Mock the HttpServletRequest and HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(request.getServletPath()).thenReturn("/api/v1/user/refreshToken");
        when(request.getServletPath()).thenReturn("/api/v1/user/logout");

        // Call the doFilterInternal method
        customAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Use reflection to call the private method
        Method method = CustomAuthorizationFilter.class.getDeclaredMethod("handleRefreshToken", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        method.invoke(customAuthorizationFilter, request, response, filterChain);


        verify(filterChain, never()).doFilter(request, response);  // handleRefreshToken should not call filterChain.doFilter
        verify(response, never()).sendError(anyInt(), anyString()); // No error should be sent
        verify(response, never()).setStatus(anyInt()); // No status change
    }

    @Test
    @DisplayName("test case for other urls starting by api/v1")
    void doFilterInternal_ApiUrls() throws ServletException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Setup: Mock the HttpServletRequest and HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(request.getServletPath()).thenReturn("/api/v1/");
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer mockToken");
        when(securityUtility.readSecretFromFile(anyString())).thenReturn("meinhuchotadon");

        // Call the doFilterInternal method
        customAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Use reflection to call the private method
        Method method = CustomAuthorizationFilter.class.getDeclaredMethod("handleAccessToken", String.class, HttpServletResponse.class, FilterChain.class, HttpServletRequest.class);
        method.setAccessible(true);
        method.invoke(customAuthorizationFilter, generateAccessToken(60), response, filterChain, request);


        verify(filterChain, times(1)).doFilter(request, response);  // handleAccessToken

    }

    @Test
    @DisplayName("test for null authorization header")
    void doFilterInternal_NullAuthorizationHeader() throws ServletException, IOException {
        // Setup: Mock the HttpServletRequest and HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(request.getHeader(AUTHORIZATION)).thenReturn(null);
        when(request.getServletPath()).thenReturn("/api/v1/someEndpoint");

        // Call the doFilterInternal method
        customAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Verify that filterChain.doFilter is called
        verify(filterChain, times(1)).doFilter(request, response);
    }






    @Test
    @DisplayName("test case for handle access token")
    void handleAccessToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Mock necessary behaviors
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer validToken");
        when(securityUtility.isAccessTokenExpired(anyString())).thenReturn(false);
        when(securityUtility.readSecretFromFile(anyString())).thenReturn("meinhuchotadon");

        // Use reflection to call the private method
        Method method = CustomAuthorizationFilter.class.getDeclaredMethod("handleAccessToken", String.class, HttpServletResponse.class, FilterChain.class, HttpServletRequest.class);
        method.setAccessible(true);
        method.invoke(customAuthorizationFilter, generateAccessToken(60), response, filterChain, request);

        // Add your verification/assertion logic here based on the behavior of your handleAccessToken method
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("test for access token expired")
    void handleAccessToken_AccessTokenExpired() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Mock necessary behaviors
        when(securityUtility.isAccessTokenExpired(anyString())).thenReturn(true);

        // Use reflection to call the private method
        Method method = CustomAuthorizationFilter.class.getDeclaredMethod("handleAccessToken", String.class, HttpServletResponse.class, FilterChain.class, HttpServletRequest.class);
        method.setAccessible(true);
        method.invoke(customAuthorizationFilter, "dummyToken", response, filterChain, request);

        // Verify that response.sendError is called with SC_UNAUTHORIZED
        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Token expired");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("test case for valid refresh token")
    void handleRefreshToken_ValidToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Mock necessary behaviors
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer validToken");
        when(securityUtility.isRefreshTokenExpired(anyString())).thenReturn(false);

        // Use reflection to call the private method
        Method method = CustomAuthorizationFilter.class.getDeclaredMethod("handleRefreshToken", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        method.invoke(customAuthorizationFilter, request, response, filterChain);

        // Verify that filterChain.doFilter is called
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("test case for expired refresh token")
    void handleRefreshToken_ExpiredToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Mock necessary behaviors
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer expiredToken");
        when(securityUtility.isRefreshTokenExpired(anyString())).thenReturn(true);

        // Use reflection to call the private method
        Method method = CustomAuthorizationFilter.class.getDeclaredMethod("handleRefreshToken", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        method.invoke(customAuthorizationFilter, request, response, filterChain);

        // Verify that response.sendError is called with SC_UNAUTHORIZED
        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token expired");
        verify(filterChain, never()).doFilter(any(), any());
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("test case for throwing exception on handling refresh token")
    void handleRefreshToken_Exception() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Mock necessary behaviors
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer tokenWithException");
        when(securityUtility.isRefreshTokenExpired(anyString())).thenThrow(new RuntimeException("Test exception message"));

        // Use reflection to call the private method
        Method method = CustomAuthorizationFilter.class.getDeclaredMethod("handleRefreshToken", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);

        // Properly mock response.getOutputStream()
        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        // Call the private method with a valid response object
        method.invoke(customAuthorizationFilter, request, response, filterChain);

        // Verify that response.sendError is not called and setStatus is called with SC_FORBIDDEN
        verify(response, never()).sendError(anyInt(), anyString());
        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response, times(1)).getOutputStream();
    }

    @Test
    @DisplayName("test case for handling unauthorized")
    void handleUnauthorized() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Create a MockHttpServletResponse
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Retrieve the private method
        Method privateMethod = CustomAuthorizationFilter.class.getDeclaredMethod("handleUnauthorized", HttpServletResponse.class);
        privateMethod.setAccessible(true);

        // Call the private method
        privateMethod.invoke(customAuthorizationFilter, response);

        // Verify the response status and content type
        Assertions.assertEquals(403, response.getStatus());
        Assertions.assertEquals("application/json", response.getContentType());
    }


    private String generateAccessToken(int expirationMinutes) throws IOException {
        String secretFilePath = "/Desktop"+"/Leaps-BE-SQ"+"/secret"+"/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
        String secret = readSecretFromFile(absolutePath);
        Algorithm algorithm = Algorithm.HMAC256(secret); // Replace "secret" with your actual secret key
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(expirationMinutes);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
        List<String> roles = new ArrayList<>();
        roles.add("OWNER");
        return JWT.create()
                .withSubject("ujohnwesly8@gmail.com")
                .withExpiresAt(expirationDate)
                .withIssuer("https://example.com")
                .withClaim("roles",roles)
                .sign(algorithm);
    }


    private String readSecretFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            return reader.readLine();
        }
    }




}