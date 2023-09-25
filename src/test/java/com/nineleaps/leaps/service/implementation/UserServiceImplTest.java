package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserLoginInfo;
import com.nineleaps.leaps.repository.UserLoginInfoRepository;
import com.nineleaps.leaps.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserLoginInfoRepository userLoginInfoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.ADMIN);
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Act
        UserDetails userDetails = userService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals("ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void loadUserByUsername_ExceptionThrownByRepository_ThrowsUsernameNotFoundException() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenThrow(new UsernameNotFoundException("Bad Credentials "));

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void loadUserByUsername_RoleAdmin_AssignsAdminAuthority() {
        // Arrange
        String email = "admin@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.ADMIN);
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Act
        UserDetails userDetails = userService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals("ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void saveDeviceTokenToUser_UserExistsWithExistingDeviceToken_UpdatesDeviceToken() {
        // Arrange
        String email = "test@example.com";
        String deviceToken = "deviceToken123";
        User user = new User();
        user.setEmail(email);
        user.setDeviceToken("oldDeviceToken");
        when(userRepository.findByEmail(email)).thenReturn(user);
        when(userRepository.findDeviceTokenByEmail(email)).thenReturn(user);

        // Act
        userService.saveDeviceTokenToUser(email, deviceToken);

        // Assert
        assertEquals(deviceToken, user.getDeviceToken());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveDeviceTokenToUser_UserExistsWithoutExistingDeviceToken_SavesDeviceToken() {
        // Arrange
        String email = "test@example.com";
        String deviceToken = "deviceToken123";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);
        when(userRepository.findDeviceTokenByEmail(email)).thenReturn(null);

        // Act
        userService.saveDeviceTokenToUser(email, deviceToken);

        // Assert
        assertEquals(deviceToken, user.getDeviceToken());
        verify(userRepository, times(1)).save(user);

    }

    @Test
    void saveDeviceTokenToUser_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        String email = "nonexistent@example.com";
        String deviceToken = "deviceToken123";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.saveDeviceTokenToUser(email, deviceToken));
    }

    @Test
    void saveDeviceTokenToUser_ExceptionThrownByRepository_ThrowsUsernameNotFoundException() {
        // Arrange
        String email = "test@example.com";
        String deviceToken = "deviceToken123";
        when(userRepository.findByEmail(email)).thenThrow(new UsernameNotFoundException("Bad Credentials "));

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.saveDeviceTokenToUser(email, deviceToken));
    }


    @Test
    void signUp_SuccessfulSignUp_UserCreated() throws CustomException {
        // Arrange
        SignupDto signupDto = new SignupDto("John", "Doe", "john.doe@example.com", "1234567890", "password", Role.BORROWER);
        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
        when(userRepository.findByPhoneNumber(signupDto.getPhoneNumber())).thenReturn(null);
        when(passwordEncoder.encode(signupDto.getPassword())).thenReturn("hashedPassword");

        // Act
        userService.signUp(signupDto);

        // Assert (add appropriate assertions based on your implementation)
        verify(userRepository, times(1)).save(any(User.class));
        verify(userLoginInfoRepository, times(1)).save(any(UserLoginInfo.class));
    }

    @Test
    void signUp_EmailAlreadyAssociatedWithOtherUser_ThrowsCustomException() {
        // Arrange
        SignupDto signupDto = new SignupDto("John", "Doe", "john.doe@example.com", "1234567890", "password", Role.BORROWER);
        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(new User());

        // Act and Assert
        assertThrows(CustomException.class, () -> userService.signUp(signupDto));
    }

    @Test
    void signUp_PhoneNumberAlreadyAssociatedWithOtherUser_ThrowsCustomException() {
        // Arrange
        SignupDto signupDto = new SignupDto("John", "Doe", "john.doe@example.com", "1234567890", "password", Role.BORROWER);
        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
        when(userRepository.findByPhoneNumber(signupDto.getPhoneNumber())).thenReturn(new User());

        // Act and Assert
        assertThrows(CustomException.class, () -> userService.signUp(signupDto));
    }

    @Test
    void signUp_ExceptionSavingToRepository_ThrowsCustomException() {
        // Arrange
        SignupDto signupDto = new SignupDto("John", "Doe", "john.doe@example.com", "1234567890", "password", Role.BORROWER);
        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
        when(userRepository.findByPhoneNumber(signupDto.getPhoneNumber())).thenReturn(null);
        when(passwordEncoder.encode(signupDto.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act and Assert
        assertThrows(CustomException.class, () -> userService.signUp(signupDto));
    }

    @Test
    void saveProfile_SaveUserProfile_UserSaved() {
        // Arrange
        User user = new User(); // create a User object with necessary data
        when(userRepository.save(user)).thenReturn(user);

        // Act
        userService.saveProfile(user);

        // Assert
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUser_GetUserByEmail_ReturnsUser() {
        // Arrange
        String email = "test@example.com";
        User expectedUser = new User();
        when(userRepository.findByEmail(email)).thenReturn(expectedUser);

        // Act
        User result = userService.getUser(email);

        // Assert
        assertEquals(expectedUser, result);
    }

    @Test
    void getUserDto_CreateUserDtoFromUser_ReturnsUserDto() {
        // Arrange
        User user = new User(); // create a User object with necessary data

        // Act
        UserDto userDto = userService.getUser(user);

        // Assert (add appropriate assertions based on your implementation)
        assertNotNull(userDto);
        // Verify that the UserDto is created accurately based on the User object
    }

    @Test
    void getGuest_UserWithRoleGuest_ReturnsGuestUser() {
        // Arrange
        User guestUser = new User();
        when(userRepository.findByRole(Role.GUEST)).thenReturn(guestUser);

        // Act
        User result = userService.getGuest();

        // Assert
        assertEquals(guestUser, result);
    }

    @Test
    void getGuest_NoGuestUser_ReturnsNull() {
        // Arrange
        when(userRepository.findByRole(Role.GUEST)).thenReturn(null);

        // Act
        User result = userService.getGuest();

        // Assert
        assertNull(result);
    }

    @Test
    void updateProfile_UpdateUserProfile_UserProfileUpdated() {
        // Arrange
        User oldUser = new User(); // create a User object with necessary data
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        profileUpdateDto.setFirstName("John");
        profileUpdateDto.setLastName("Doe");
        oldUser.setFirstName(profileUpdateDto.getFirstName());
        oldUser.setLastName(profileUpdateDto.getLastName());
        // Act
        userService.updateProfile(oldUser, profileUpdateDto);

        // Assert
        assertEquals("John", oldUser.getFirstName());
        assertEquals("Doe", oldUser.getLastName());
    }

    @Test
    void updateProfileImage_UpdateUserProfileImage_UserProfileImageUpdated() {
        // Arrange
        User user = new User(); // create a User object with necessary data
        String profileImageUrl = "https://example.com/profile-image";

        // Act
        userService.updateProfileImage(profileImageUrl, user);

        // Assert
        assertEquals(profileImageUrl, user.getProfileImageUrl());
    }

    @Test
    void getUserViaPhoneNumber_ValidPhoneNumber_ReturnsUser() {
        // Arrange
        String phoneNumber = "1234567890";
        User expectedUser = new User();
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(expectedUser);

        // Act
        User result = userService.getUserViaPhoneNumber(phoneNumber);

        // Assert
        assertEquals(expectedUser, result);
    }

    @Test
    void getUserViaPhoneNumber_InvalidPhoneNumber_ReturnsNull() {
        // Arrange
        String phoneNumber = "invalidPhoneNumber";
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(null);

        // Act
        User result = userService.getUserViaPhoneNumber(phoneNumber);

        // Assert
        assertNull(result);
    }

    @Test
    void getUsers_GetAllUsers_ReturnsListOfUsers() {
        // Arrange
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<User> result = userService.getUsers();

        // Assert
        assertNotNull(result);
        assertEquals(userList.size(), result.size());
    }

    @Test
    void updateProfileImage_UrlContainsNgrok_ShouldRemoveNgrokFromImageUrl() {
        // Arrange
        User user = new User();
        String profileImageUrl = "https://c540-180-151-122-199.ngrok-free.app/profile-image";

        // Act
        userService.updateProfileImage(profileImageUrl, user);

        // Assert
        assertEquals("https://c540-180-151-122-199.ngrok-free.app/profile-image", profileImageUrl); // profileImageUrl should not be changed
        assertEquals("/profile-image", user.getProfileImageUrl());
    }

    @Test
    void updateProfileImage_UrlDoesNotContainNgrok_ShouldKeepOriginalImageUrl() {
        // Arrange
        User user = new User();
        String profileImageUrl = "https://example.com/profile-image";

        // Act
        userService.updateProfileImage(profileImageUrl, user);

        // Assert
        assertEquals("https://example.com/profile-image", user.getProfileImageUrl());
    }

}