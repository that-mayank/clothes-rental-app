//package com.nineleaps.leaps.service.implementation;
//
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
//import com.nineleaps.leaps.dto.user.SignupDto;
//import com.nineleaps.leaps.dto.user.UserDto;
//import com.nineleaps.leaps.enums.Role;
//import com.nineleaps.leaps.exceptions.CustomException;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.model.UserLoginInfo;
//import com.nineleaps.leaps.repository.UserLoginInfoRepository;
//import com.nineleaps.leaps.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.nineleaps.leaps.LeapsApplication.NGROK;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.when;
//@Tag("unit_tests")
//@DisplayName("User Service Tests")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//class UserServiceImplTest {
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private UserLoginInfoRepository userLoginInfoRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("Load User By Username - User Exists - Returns UserDetails")
//    void loadUserByUsername_UserExists_ReturnsUserDetails() {
//        // Arrange
//        String email = "test@example.com";
//        User user = new User();
//        user.setEmail(email);
//        user.setPassword("password");
//        user.setRole(Role.ADMIN);
//        when(userRepository.findByEmail(email)).thenReturn(user);
//
//        // Act
//        UserDetails userDetails = userService.loadUserByUsername(email);
//
//        // Assert
//        assertNotNull(userDetails);
//        assertEquals(email, userDetails.getUsername());
//        assertEquals("password", userDetails.getPassword());
//        assertEquals("ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
//    }
//
//    @Test
//    @DisplayName("Load User By Username - User Does Not Exist - Throws UsernameNotFoundException")
//    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
//        // Arrange
//        String email = "nonexistent@example.com";
//        when(userRepository.findByEmail(email)).thenReturn(null);
//
//        // Act and Assert
//        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
//    }
//
//   @Test
//    void loadUserByUsername_ExceptionThrownByRepository_ThrowsUsernameNotFoundException() {
//        // Arrange
//        String email = "test@example.com";
//        when(userRepository.findByEmail(email)).thenThrow(new UsernameNotFoundException("Bad Credentials "));
//
//        // Act and Assert
//        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
//    }
//
//    @Test
//
//    void loadUserByUsername_RoleAdmin_AssignsAdminAuthority() {
//        // Arrange
//        String email = "admin@example.com";
//        User user = new User();
//        user.setEmail(email);
//        user.setPassword("password");
//        user.setRole(Role.ADMIN);
//        when(userRepository.findByEmail(email)).thenReturn(user);
//
//        // Act
//        UserDetails userDetails = userService.loadUserByUsername(email);
//
//        // Assert
//        assertNotNull(userDetails);
//        assertEquals(email, userDetails.getUsername());
//        assertEquals("password", userDetails.getPassword());
//        assertEquals("ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
//    }
//
//    @Test
//    @DisplayName("Save Device Token To User - User Exists With Existing Device Token - Updates Device Token")
//    void saveDeviceTokenToUser_UserExistsWithExistingDeviceToken_UpdatesDeviceToken() {
//        // Arrange
//        String email = "test@example.com";
//        String deviceToken = "deviceToken123";
//        User user = new User();
//        user.setEmail(email);
//        user.setDeviceToken("oldDeviceToken");
//        when(userRepository.findByEmail(email)).thenReturn(user);
//        when(userRepository.findDeviceTokenByEmail(email)).thenReturn(user);
//
//        // Act
//        userService.saveDeviceTokenToUser(email, deviceToken);
//
//        // Assert
//        assertEquals(deviceToken, user.getDeviceToken());
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    @DisplayName("Save Device Token To User - User Exists Without Existing Device Token - Saves Device Token")
//    void saveDeviceTokenToUser_UserExistsWithoutExistingDeviceToken_SavesDeviceToken() {
//        // Arrange
//        String email = "test@example.com";
//        String deviceToken = "deviceToken123";
//        User user = new User();
//        user.setEmail(email);
//        when(userRepository.findByEmail(email)).thenReturn(user);
//        when(userRepository.findDeviceTokenByEmail(email)).thenReturn(null);
//
//        // Act
//        userService.saveDeviceTokenToUser(email, deviceToken);
//
//        // Assert
//        assertEquals(deviceToken, user.getDeviceToken());
//        verify(userRepository, times(1)).save(user);
//
//    }
//
//    @Test
//    @DisplayName("Save Device Token To User - User Does Not Exist - Throws UsernameNotFoundException")
//    void saveDeviceTokenToUser_UserDoesNotExist_ThrowsUsernameNotFoundException() {
//        // Arrange
//        String email = "nonexistent@example.com";
//        String deviceToken = "deviceToken123";
//        when(userRepository.findByEmail(email)).thenReturn(null);
//
//        // Act and Assert
//        assertThrows(UsernameNotFoundException.class, () -> userService.saveDeviceTokenToUser(email, deviceToken));
//    }
//
//    @Test
//    void saveDeviceTokenToUser_ExceptionThrownByRepository_ThrowsUsernameNotFoundException() {
//        // Arrange
//        String email = "test@example.com";
//        String deviceToken = "deviceToken123";
//        when(userRepository.findByEmail(email)).thenThrow(new UsernameNotFoundException("Bad Credentials "));
//
//        // Act and Assert
//        assertThrows(UsernameNotFoundException.class, () -> userService.saveDeviceTokenToUser(email, deviceToken));
//    }
//
//
//    @Test
//    @DisplayName("Sign Up - Successful Sign Up - User Created")
//    void signUp_SuccessfulSignUp_UserCreated() throws CustomException {
//        // Arrange
//        SignupDto signupDto = new SignupDto("John", "Doe", "john.doe@example.com", "1234567890", "password", Role.BORROWER);
//        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
//        when(userRepository.findByPhoneNumber(signupDto.getPhoneNumber())).thenReturn(null);
//        when(passwordEncoder.encode(signupDto.getPassword())).thenReturn("hashedPassword");
//
//        // Act
//        userService.signUp(signupDto);
//
//        // Assert (add appropriate assertions based on your implementation)
//        verify(userRepository, times(1)).save(any(User.class));
//        verify(userLoginInfoRepository, times(1)).save(any(UserLoginInfo.class));
//    }
//
//    @Test
//    @DisplayName("Sign Up - Email Already Associated With Other User - Throws CustomException")
//    void signUp_EmailAlreadyAssociatedWithOtherUser_ThrowsCustomException() {
//        // Arrange
//        SignupDto signupDto = new SignupDto("John", "Doe", "john.doe@example.com", "1234567890", "password", Role.BORROWER);
//        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(new User());
//
//        // Act and Assert
//        assertThrows(CustomException.class, () -> userService.signUp(signupDto));
//    }
//
//    @Test
//    @DisplayName("Sign Up - Phone Number Already Associated With Other User - Throws CustomException")
//    void signUp_PhoneNumberAlreadyAssociatedWithOtherUser_ThrowsCustomException() {
//        // Arrange
//        SignupDto signupDto = new SignupDto("John", "Doe", "john.doe@example.com", "1234567890", "password", Role.BORROWER);
//        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
//        when(userRepository.findByPhoneNumber(signupDto.getPhoneNumber())).thenReturn(new User());
//
//        // Act and Assert
//        assertThrows(CustomException.class, () -> userService.signUp(signupDto));
//    }
//
//    @Test
//    void signUp_ExceptionSavingToRepository_ThrowsCustomException() {
//        // Arrange
//        SignupDto signupDto = new SignupDto("John", "Doe", "john.doe@example.com", "1234567890", "password", Role.BORROWER);
//        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
//        when(userRepository.findByPhoneNumber(signupDto.getPhoneNumber())).thenReturn(null);
//        when(passwordEncoder.encode(signupDto.getPassword())).thenReturn("hashedPassword");
//        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));
//
//        // Act and Assert
//        assertThrows(CustomException.class, () -> userService.signUp(signupDto));
//    }
//
//    @Test
//    @DisplayName("Save Profile - Save User Profile - User Saved")
//    void saveProfile_SaveUserProfile_UserSaved() {
//        // Arrange
//        User user = new User(); // create a User object with necessary data
//        when(userRepository.save(user)).thenReturn(user);
//
//        // Act
//        userService.saveProfile(user);
//
//        // Assert
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    @DisplayName("Get user by email")
//    void getUser_GetUserByEmail_ReturnsUser() {
//        // Arrange
//        String email = "test@example.com";
//        User expectedUser = new User();
//        when(userRepository.findByEmail(email)).thenReturn(expectedUser);
//
//        // Act
//        User result = userService.getUser(email);
//
//        // Assert
//        assertEquals(expectedUser, result);
//    }
//
//    @Test
//    @DisplayName("Get userDto")
//    void getUserDto_CreateUserDtoFromUser_ReturnsUserDto() {
//        // Arrange
//        User user = new User(); // create a User object with necessary data
//
//        // Act
//        UserDto userDto = userService.getUser(user);
//
//        // Assert (add appropriate assertions based on your implementation)
//        assertNotNull(userDto);
//        // Verify that the UserDto is created accurately based on the User object
//    }
//
//    @Test
//    @DisplayName("Get guest user")
//    void getGuest_UserWithRoleGuest_ReturnsGuestUser() {
//        // Arrange
//        User guestUser = new User();
//        when(userRepository.findByRole(Role.GUEST)).thenReturn(guestUser);
//
//        // Act
//        User result = userService.getGuest();
//
//        // Assert
//        assertEquals(guestUser, result);
//    }
//
//    @Test
//    @DisplayName("Get guest user")
//    void getGuest_NoGuestUser_ReturnsNull() {
//        // Arrange
//        when(userRepository.findByRole(Role.GUEST)).thenReturn(null);
//
//        // Act
//        User result = userService.getGuest();
//
//        // Assert
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Update Profile - Update User Profile - User Profile Updated")
//    void updateProfile_UpdateUserProfile_UserProfileUpdated() {
//        // Arrange
//        User oldUser = new User(); // create a User object with necessary data
//        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
//        profileUpdateDto.setFirstName("John");
//        profileUpdateDto.setLastName("Doe");
//        oldUser.setFirstName(profileUpdateDto.getFirstName());
//        oldUser.setLastName(profileUpdateDto.getLastName());
//        // Act
//        userService.updateProfile(oldUser, profileUpdateDto);
//
//        // Assert
//        assertEquals("John", oldUser.getFirstName());
//        assertEquals("Doe", oldUser.getLastName());
//    }
//
//    @Test
//    @DisplayName("Update Profile Image - Update User Profile Image - User Profile Image Updated")
//    void updateProfileImage_UpdateUserProfileImage_UserProfileImageUpdated() {
//        // Arrange
//        User user = new User(); // create a User object with necessary data
//        String profileImageUrl = "https://example.com/profile-image";
//
//        // Act
//        userService.updateProfileImage(profileImageUrl, user);
//
//        // Assert
//        assertEquals(profileImageUrl, user.getProfileImageUrl());
//    }
//
//    @Test
//    @DisplayName("Get User Via Phone Number - Valid Phone Number - Returns User")
//    void getUserViaPhoneNumber_ValidPhoneNumber_ReturnsUser() {
//        // Arrange
//        String phoneNumber = "1234567890";
//        User expectedUser = new User();
//        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(expectedUser);
//
//        // Act
//        User result = userService.getUserViaPhoneNumber(phoneNumber);
//
//        // Assert
//        assertEquals(expectedUser, result);
//    }
//
//    @Test
//    @DisplayName("Get  user via phonenumber - return null")
//    void getUserViaPhoneNumber_InvalidPhoneNumber_ReturnsNull() {
//        // Arrange
//        String phoneNumber = "invalidPhoneNumber";
//        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(null);
//
//        // Act
//        User result = userService.getUserViaPhoneNumber(phoneNumber);
//
//        // Assert
//        assertNull(result);
//    }
//
//    @Test
//    void getUsers_GetAllUsers_ReturnsListOfUsers() {
//        // Arrange
//        List<User> userList = new ArrayList<>();
//        userList.add(new User());
//        when(userRepository.findAll()).thenReturn(userList);
//
//        // Act
//        List<User> result = userService.getUsers();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(userList.size(), result.size());
//    }
//
//    @Test
//    @DisplayName("Update Profile Image - URL Contains Ngrok - Should Remove Ngrok From Image URL")
//    void updateProfileImage_UrlContainsNgrok_ShouldRemoveNgrokFromImageUrl() {
//        // Arrange
//        User user = new User();
//        String profileImageUrl = NGROK+"/profile-image";
//
//        // Act
//        userService.updateProfileImage(profileImageUrl, user);
//
//        // Assert
//        assertEquals(NGROK+"/profile-image", profileImageUrl); // profileImageUrl should not be changed
//        assertEquals("/profile-image", user.getProfileImageUrl());
//    }
//
//    @Test
//    @DisplayName("Update Profile Image - URL Does Not Contain Ngrok - Should Keep Original Image URL")
//    void updateProfileImage_UrlDoesNotContainNgrok_ShouldKeepOriginalImageUrl() {
//        // Arrange
//        User user = new User();
//        String profileImageUrl = "https://example.com/profile-image";
//
//        // Act
//        userService.updateProfileImage(profileImageUrl, user);
//
//        // Assert
//        assertEquals("https://example.com/profile-image", user.getProfileImageUrl());
//    }
//
//}