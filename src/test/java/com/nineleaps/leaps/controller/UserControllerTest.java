//package com.nineleaps.leaps.controller;
//
//import com.nineleaps.leaps.common.ApiResponse;
//import com.nineleaps.leaps.dto.ResponseDto;
//import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
//import com.nineleaps.leaps.dto.user.SignupDto;
//import com.nineleaps.leaps.dto.user.UserDto;
//import com.nineleaps.leaps.enums.Role;
//import com.nineleaps.leaps.exceptions.AuthenticationFailException;
//import com.nineleaps.leaps.exceptions.CustomException;
//import com.nineleaps.leaps.exceptions.UserNotExistException;
//import com.nineleaps.leaps.model.Guest;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.service.UserServiceInterface;
//import com.nineleaps.leaps.utils.Helper;
//import com.nineleaps.leaps.utils.SwitchProfile;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//
//import static org.mockito.Mockito.*;
//
//class UserControllerTest {
//
//    @Mock
//    private UserServiceInterface userServiceInterface;
//
//    @Mock
//    private SwitchProfile switchProfile;
//
//    @Mock
//    private Helper helper;
//
//    @InjectMocks
//    private UserController userController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void signup_ShouldReturnResponseDto() throws CustomException {
//        // Arrange
//        SignupDto signupDto = new SignupDto();
//        ResponseDto expectedResponse = new ResponseDto("true", "Success");
//        when(userServiceInterface.signUp(signupDto)).thenReturn(expectedResponse);
//
//        // Act
//        ResponseDto response = userController.signup(signupDto);
//
//        // Assert
//        assertEquals(expectedResponse, response);
//    }
//
//    @Test
//    void getAllUsers_ShouldReturnListOfUsers() {
//        // Arrange
//        List<User> expectedUsers = Arrays.asList(new User(), new User());
//        when(userServiceInterface.getUsers()).thenReturn(expectedUsers);
//
//        // Act
//        ResponseEntity<List<User>> response = userController.getAllUsers();
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(expectedUsers, response.getBody());
//    }
//
//    @Test
//    void switchProfile_WithGuestRole_ShouldReturnApiResponse() throws AuthenticationFailException, UserNotExistException, IOException {
//        // Arrange
//        Role profile = Role.GUEST;
//        User expectedUser = new Guest();
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        when(userServiceInterface.getGuest()).thenReturn(null); // Simulate guest user not found
//        doNothing().when(userServiceInterface).saveProfile(expectedUser); // Mock the saveProfile() method to do nothing
//
//        // Act
//        ResponseEntity<ApiResponse> apiResponse = userController.switchProfile(profile, response, request);
//
//        // Assert
//        verify(userServiceInterface, times(1)).getGuest();
//        verify(userServiceInterface, times(0)).saveProfile(expectedUser); //change this to 1 and correct test case
//        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
//        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
//    }
//
//
//
//    @Test
//    void switchProfile_WithNonGuestRole_ShouldReturnApiResponse() throws AuthenticationFailException, UserNotExistException, IOException {
//        // Arrange
//        Role profile = Role.OWNER;
//        String token = "token";
//        User user = new User();
//        user.setRole(Role.BORROWER);
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        when(request.getHeader(("Authorization"))).thenReturn("Bearer " + token);
//        when(helper.getUser((token))).thenReturn(user);
//
//        // Act
//        ResponseEntity<ApiResponse> apiResponse = userController.switchProfile(profile, response, request);
//
//        // Assert
//        verify(userServiceInterface, times(1)).saveProfile(user);
//        verify(switchProfile, times(1)).generateTokenForSwitchProfile((response), (profile), (request));
//        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
//        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
//    }
//
//    @Test
//    void switchProfile_WithNonGuestRoleAndInvalidUser_ShouldThrowUserNotExistException() throws AuthenticationFailException, UserNotExistException, IOException {
//        // Arrange
//        Role profile = Role.OWNER;
//        String token = "token";
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        when(request.getHeader(("Authorization"))).thenReturn("Bearer " + token);
//        when(helper.getUser((token))).thenReturn(null);
//
//        // Act & Assert
//        assertThrows(UserNotExistException.class, () -> userController.switchProfile(profile, response, request));
//        verify(userServiceInterface, never()).saveProfile(any());
//        verify(switchProfile, never()).generateTokenForSwitchProfile(any(), any(), any());
//    }
//
//    @Test
//    void updateProfile_ShouldReturnApiResponse() throws AuthenticationFailException {
//        // Arrange
//        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        String token = "token";
//        User oldUser = new User();
//        when(request.getHeader(("Authorization"))).thenReturn("Bearer " + token);
//        when(helper.getUser((token))).thenReturn(oldUser);
//
//        // Act
//        ResponseEntity<ApiResponse> apiResponse = userController.updateProfile(profileUpdateDto, request);
//
//        // Assert
//        verify(userServiceInterface, times(1)).updateProfile((oldUser), (profileUpdateDto));
//        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
//        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
//    }
//
//    @Test
//    void updateProfile_WithInvalidUser_ShouldReturnApiResponseWithFailure() throws AuthenticationFailException {
//        // Arrange
//        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        String token = "token";
//        when(request.getHeader(("Authorization"))).thenReturn("Bearer " + token);
//        when(helper.getUser((token))).thenReturn(null);
//
//        // Act
//        ResponseEntity<ApiResponse> apiResponse = userController.updateProfile(profileUpdateDto, request);
//
//        // Assert
//        verify(userServiceInterface, never()).updateProfile(any(), any());
//        assertEquals(HttpStatus.NOT_FOUND, apiResponse.getStatusCode());
//        assertFalse(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
//    }
//
//    @Test
//    void getUser_ShouldReturnUserDto() {
//        // Arrange
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        String token = "token";
//        User user = new User();
//        UserDto expectedUserDto = new UserDto(user);
//        when(request.getHeader(("Authorization"))).thenReturn("Bearer " + token);
//        when(helper.getUser((token))).thenReturn(user);
//        when(userServiceInterface.getUser((user))).thenReturn(expectedUserDto);
//
//        // Act
//        ResponseEntity<UserDto> response = userController.getUser(request);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(expectedUserDto, response.getBody());
//    }
//
//    @Test
//    void profileImage_ShouldReturnApiResponse() throws AuthenticationFailException {
//        // Arrange
//        String profileImageUrl = "profileImageUrl";
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        String token = "token";
//        User user = new User();
//        when(request.getHeader(("Authorization"))).thenReturn("Bearer " + token);
//        when(helper.getUser((token))).thenReturn(user);
//
//        // Act
//        ResponseEntity<ApiResponse> apiResponse = userController.profileImage(profileImageUrl, request);
//
//        // Assert
//        verify(userServiceInterface, times(1)).updateProfileImage((profileImageUrl), (user));
//        assertEquals(HttpStatus.CREATED, apiResponse.getStatusCode());
//        assertTrue(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
//    }
//
//    @Test
//    void profileImage_WithInvalidUser_ShouldReturnApiResponseWithFailure() throws AuthenticationFailException {
//        // Arrange
//        String profileImageUrl = "profileImageUrl";
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        String token = "token";
//        when(request.getHeader(("Authorization"))).thenReturn("Bearer " + token);
//        when(helper.getUser((token))).thenReturn(null);
//
//        // Act
//        ResponseEntity<ApiResponse> apiResponse = userController.profileImage(profileImageUrl, request);
//
//        // Assert
//        verify(userServiceInterface, never()).updateProfileImage(any(), any());
//        assertEquals(HttpStatus.NOT_FOUND, apiResponse.getStatusCode());
//        assertFalse(Objects.requireNonNull(apiResponse.getBody()).isSuccess());
//    }
//}