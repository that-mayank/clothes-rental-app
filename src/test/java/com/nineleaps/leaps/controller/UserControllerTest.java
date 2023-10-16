package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.enums.Role;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserServiceInterface userServiceInterface;

    @Mock
    private SwitchProfile switchProfile;

    @Mock
    private Helper helper;

    @Mock
    private RefreshTokenServiceInterface refreshTokenService;

    @Mock
    private SecurityUtility securityUtility;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_ShouldReturnResponseDto() throws CustomException {
        // Arrange
        SignupDto signupDto = new SignupDto();
        ResponseDto expectedResponse = new ResponseDto("true", "Success");
        when(userServiceInterface.signUp(signupDto)).thenReturn(expectedResponse);

        // Act
        ResponseDto response = userController.signup(signupDto);

        // Assert
        assertEquals(expectedResponse, response);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<UserDto> expectedUsers = List.of(new UserDto(new User()), new UserDto(new User()));
        when(userServiceInterface.getUsers()).thenReturn(expectedUsers);

        // Act
        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
    }

    @Test
    void switchProfile_WithNonGuestRole_ShouldReturnApiResponse() throws  UserNotExistException, IOException {
        // Arrange
        Role profile = Role.OWNER;
        User user = new User();
        user.setRole(Role.BORROWER);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(helper.getUser((request))).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> apiResponse = userController.switchProfile(profile, response, request);

        // Assert
        verify(userServiceInterface, times(1)).saveProfile(user);
        verify(switchProfile, times(1)).generateTokenForSwitchProfile((response), (profile), (request));
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void switchProfile_WithNonGuestRoleAndInvalidUser_ShouldThrowUserNotExistException() throws  UserNotExistException, IOException {
        // Arrange
        Role profile = Role.OWNER;
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(helper.getUser((request))).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotExistException.class, () -> userController.switchProfile(profile, response, request));
        verify(userServiceInterface, never()).saveProfile(any());
        verify(switchProfile, never()).generateTokenForSwitchProfile(any(), any(), any());
    }

    @Test
    void updateProfile_ShouldReturnApiResponse() {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User oldUser = new User();
        when(helper.getUser((request))).thenReturn(oldUser);

        // Act
        ResponseEntity<ApiResponse> apiResponse = userController.updateProfile(profileUpdateDto, request);

        // Assert
        verify(userServiceInterface, times(1)).updateProfile((oldUser), (profileUpdateDto));
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void updateProfile_WithInvalidUser_ShouldReturnApiResponseWithFailure() {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(helper.getUser((request))).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> apiResponse = userController.updateProfile(profileUpdateDto, request);

        // Assert
        verify(userServiceInterface, never()).updateProfile(any(), any());
        assertEquals(HttpStatus.NOT_FOUND, apiResponse.getStatusCode());
        assertFalse(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void getUser_ShouldReturnUserDto() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        UserDto expectedUserDto = new UserDto(user);
        when(helper.getUser((request))).thenReturn(user);
        when(userServiceInterface.getUser((user))).thenReturn(expectedUserDto);

        // Act
        ResponseEntity<UserDto> response = userController.getUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDto, response.getBody());
    }

    @Test
    void profileImage_ShouldReturnApiResponse() {
        // Arrange
        String profileImageUrl = "profileImageUrl";
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        when(helper.getUser((request))).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> apiResponse = userController.profileImage(profileImageUrl, request);

        // Assert
        verify(userServiceInterface, times(1)).updateProfileImage((profileImageUrl), (user));
        assertEquals(HttpStatus.CREATED, apiResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void profileImage_WithInvalidUser_ShouldReturnApiResponseWithFailure()  {
        // Arrange
        String profileImageUrl = "profileImageUrl";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(helper.getUser((request))).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> apiResponse = userController.profileImage(profileImageUrl, request);

        // Assert
        verify(userServiceInterface, never()).updateProfileImage(any(), any());
        assertEquals(HttpStatus.NOT_FOUND, apiResponse.getStatusCode());
        assertFalse(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void updateTokenUsingRefreshToken_ValidRefreshToken_ShouldReturnApiResponse() throws IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String token = "valid_refresh_token";
        User user = new User();
        user.setEmail("user@example.com");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(securityUtility.isTokenExpired(token)).thenReturn(true);
        when(helper.getUser(request)).thenReturn(user);
        when(securityUtility.updateAccessTokenViaRefreshToken(user.getEmail(), request, token)).thenReturn("new_access_token");

        // Act
        ResponseEntity<ApiResponse> apiResponse = userController.updateTokenUsingRefreshToken(request, response);

        // Assert
        verify(securityUtility, times(1)).isTokenExpired(token);
        verify(securityUtility, times(1)).updateAccessTokenViaRefreshToken(user.getEmail(), request, token);
        verify(response, times(1)).setHeader("access_token", "new_access_token");
        assertEquals(HttpStatus.CREATED, apiResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void updateTokenUsingRefreshToken_InvalidRefreshToken_ShouldReturnApiResponseWithFailure() throws  IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String token = "expired_refresh_token";
        User user = new User();
        user.setEmail("user@example.com");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(securityUtility.isTokenExpired(token)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse> apiResponse = userController.updateTokenUsingRefreshToken(request, response);

        // Assert
        verify(securityUtility, times(1)).isTokenExpired(token);
        verify(securityUtility, times(0)).updateAccessTokenViaRefreshToken(user.getEmail(), request, token);
        verify(response, never()).setHeader(any(), any());
        assertEquals(HttpStatus.UNAUTHORIZED, apiResponse.getStatusCode());
        assertFalse(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

    @Test
    void logout_ShouldReturnApiResponse() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String token = "token";
        User user = new User();
        user.setEmail("user@example.com");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(helper.getUser(request)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> apiResponse = userController.logout(request);

        // Assert
        verify(refreshTokenService, times(1)).deleteRefreshTokenByEmailAndToken(user.getEmail(), token);
        assertEquals(HttpStatus.CREATED, apiResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
    }

}