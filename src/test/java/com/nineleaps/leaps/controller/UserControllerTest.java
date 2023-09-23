package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;

import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.UserNotExistException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.RefreshTokenServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import com.nineleaps.leaps.utils.SecurityUtility;
import com.nineleaps.leaps.utils.SwitchProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserController userController;

    @Mock
    private UserServiceInterface userServiceInterface;

    @Mock
    private SwitchProfile switchProfile;

    @Mock
    private Helper helper;

    @Mock
    private SecurityUtility securityUtility;

    @Mock
    private RefreshTokenServiceInterface refreshTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userServiceInterface, switchProfile, helper, securityUtility, refreshTokenService);
    }

    @Test
    void signup_shouldReturnCreatedResponse() throws CustomException {
        // Arrange
        SignupDto signupDto = new SignupDto();
        ResponseEntity<ApiResponse> expectedResponse = new ResponseEntity<>(new ApiResponse(true, "SignedUp Successfully"), HttpStatus.CREATED);

        // Act
        doNothing().when(userServiceInterface).signUp(signupDto);
        ResponseEntity<ApiResponse> response = userController.signup(signupDto);

        // Assert
        verify(userServiceInterface).signUp(signupDto);
        assertResponseEquals(expectedResponse, response);
    }



    @Test
    void updateProfile_shouldReturnOkResponse() throws AuthenticationFailException {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User oldUser = new User();

        // Act
        when(helper.getUserFromToken(request)).thenReturn(oldUser);
        doNothing().when(userServiceInterface).updateProfile(oldUser, profileUpdateDto);
        ResponseEntity<ApiResponse> response = userController.updateProfile(profileUpdateDto, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getUser_shouldReturnOkResponse() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        UserDto userDto = new UserDto();

        // Act
        when(helper.getUserFromToken(request)).thenReturn(user);
        when(userServiceInterface.getUser(user)).thenReturn(userDto);
        ResponseEntity<UserDto> response = userController.getUser(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void profileImage_shouldReturnCreatedResponse() throws AuthenticationFailException {
        // Arrange
        String profileImageUrl = "https://example.com/image.jpg";
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        // Act
        when(helper.getUserFromToken(request)).thenReturn(user);
        doNothing().when(userServiceInterface).updateProfileImage(profileImageUrl, user);
        ResponseEntity<ApiResponse> response = userController.profileImage(profileImageUrl, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    @Test
    void profileImage_shouldReturnNotFound_whenUserNotFound() throws AuthenticationFailException {
        // Arrange
        String profileImageUrl = "https://example.com/image.jpg";
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act
        when(helper.getUserFromToken(request)).thenReturn(null);

        // Assert
        ResponseEntity<ApiResponse> response = userController.profileImage(profileImageUrl, request);

        // Validate the response
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("User is invalid", responseBody.getMessage());
    }


    @Test
    void updateTokenUsingRefreshToken_shouldReturnCreatedResponse() throws AuthenticationFailException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String authorizationHeader = "Bearer someAccessToken";
        String token = authorizationHeader.substring(7);
        User user = new User();
        user.setEmail("test@example.com");

        // Act
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser(token)).thenReturn(user);
        when(securityUtility.updateAccessTokenViaRefreshToken(user.getEmail(), request, token)).thenReturn("newAccessToken");

        ResponseEntity<ApiResponse> apiResponse = userController.updateTokenUsingRefreshToken(request, response);

        // Assert
        assertNotNull(apiResponse);
        assertEquals(HttpStatus.CREATED, apiResponse.getStatusCode());
    }

    @Test
    void switchProfile_shouldReturnRoleSwitchedResponse() throws AuthenticationFailException, UserNotExistException, IOException {
        // Arrange
        Role profile = Role.OWNER;
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setRole(Role.BORROWER);

        // Act
        when(helper.getUserFromToken(request)).thenReturn(user);
        ResponseEntity<ApiResponse> apiResponse = userController.switchProfile(profile, response, request);

        // Assert
        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals("Role switch to: " + profile, apiResponse.getBody().getMessage());
    }

    @Test
    void switchProfile_shouldThrowUserNotExistException_whenUserIsNull() throws AuthenticationFailException, UserNotExistException, IOException {
        // Arrange
        Role profile = Role.OWNER;
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act
        when(helper.getUserFromToken(request)).thenReturn(null);

        // Assert
        UserNotExistException thrownException = assertThrows(UserNotExistException.class, () ->
                userController.switchProfile(profile, response, request));

        assertEquals("User is invalid", thrownException.getMessage());
    }



    @Test
    void logout_shouldReturnOkResponse() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer someAccessToken";
        String token = authorizationHeader.substring(7);
        User user = new User();
        user.setEmail("test@example.com");

        // Act
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser(token)).thenReturn(user);
        doNothing().when(refreshTokenService).deleteRefreshTokenByEmailAndToken(user.getEmail(), token);

        ResponseEntity<ApiResponse> apiResponse = userController.logout(request);

        // Assert
        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
    }

    private void assertResponseEquals(ResponseEntity<ApiResponse> expected, ResponseEntity<ApiResponse> actual) {
        assertEquals(expected.getStatusCode(), actual.getStatusCode());
        assertEquals(Objects.requireNonNull(expected.getBody()).isSuccess(), Objects.requireNonNull(actual.getBody()).isSuccess());
        assertEquals(expected.getBody().getMessage(), actual.getBody().getMessage());
    }
}
