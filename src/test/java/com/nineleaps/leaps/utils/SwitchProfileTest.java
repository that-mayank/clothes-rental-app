//package com.nineleaps.leaps.utils;
//
//import com.nineleaps.leaps.enums.Role;
//import com.nineleaps.leaps.model.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@MockitoSettings(strictness = Strictness.LENIENT)
//@ExtendWith(MockitoExtension.class)
//class SwitchProfileTest {
//    private SwitchProfile switchProfile;
//private SecurityUtility securityUtility;
//    @Mock
//    private Helper helper;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        switchProfile = new SwitchProfile(helper,securityUtility);
//    }
//
//    @Test
//    void generateTokenForSwitchProfile_ValidToken_Success() throws IOException {
//        // Arrange
//        String token = "validToken";
//        Role profile = Role.ADMIN;
//        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest
//        HttpServletResponse response = new MockHttpServletResponse();
//
//        // Mock the behavior of the request.getHeader method
//        when(request.getHeader(("Authorization"))).thenReturn("Bearer " + token);
//
//        // Mock the behavior of the helper class
//        User user = new User();
//        when(helper.getUser(token)).thenReturn(user);
//
//        // Act
//        assertDoesNotThrow(() -> switchProfile.generateTokenForSwitchProfile(response, profile, request));
//
//        // Assert
//        String accessToken = response.getHeader("access_token");
//        assertNotNull(accessToken);
//        // Add more assertions as per your requirements
//    }
//
//    @Test
//    void generateTokenForSwitchProfile_InvalidToken_ThrowsException() {
//        // Arrange
//        String invalidToken = "invalidToken";
//        Role profile = Role.ADMIN;
//        HttpServletRequest request = new MockHttpServletRequest();
//        HttpServletResponse response = new MockHttpServletResponse();
//
//        // Mock the behavior of the helper class to throw a RuntimeException
//        doThrow(new RuntimeException("Failed to get user")).when(helper).getUser(invalidToken);
//
//        // Act & Assert
//        assertThrows(RuntimeException.class, () -> switchProfile.generateTokenForSwitchProfile(response, profile, request));
//    }
//
//
//
//}
