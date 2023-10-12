package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.enums.ResponseStatus;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

import static com.nineleaps.leaps.config.MessageStrings.USER_CREATED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserFoundInDatabase_ReturnsUserDetails() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.BORROWER);

        when(userRepository.findByEmail(email)).thenReturn(user);

        // Act
        org.springframework.security.core.userdetails.UserDetails userDetails = userService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals(user.getRole().toString(), authorities.iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_UserNotFoundInDatabase_ThrowsUsernameNotFoundException() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void signUp_ValidSignupDto_ReturnsSuccessResponseDto() throws CustomException {
        // Arrange
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("test@example.com");
        signupDto.setPassword("password");
        signupDto.setRole(Role.BORROWER);

        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
        when(userRepository.findByPhoneNumber(signupDto.getPhoneNumber())).thenReturn(null);
        when(passwordEncoder.encode(signupDto.getPassword())).thenReturn("encryptedPassword");

        // Act
        ResponseDto responseDto = userService.signUp(signupDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals(ResponseStatus.SUCCESS.toString(), responseDto.getStatus());
        assertEquals(USER_CREATED, responseDto.getMessage());

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals(signupDto.getEmail(), savedUser.getEmail());
        assertEquals("encryptedPassword", savedUser.getPassword());
        assertEquals(signupDto.getRole(), savedUser.getRole());
    }

    @Test
    void signUp_EmailAlreadyRegistered_ThrowsCustomException() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("test@example.com");
        signupDto.setPassword("password");
        signupDto.setRole(Role.BORROWER);

        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(new User());

        // Act and Assert
        assertThrows(CustomException.class, () -> userService.signUp(signupDto));
    }

    @Test
    void signUp_PhoneNumberAlreadyRegistered_ThrowsCustomException() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("test@example.com");
        signupDto.setPhoneNumber("1234567890");
        signupDto.setPassword("password");
        signupDto.setRole(Role.BORROWER);

        when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
        when(userRepository.findByPhoneNumber(signupDto.getPhoneNumber())).thenReturn(new User());

        // Act and Assert
        assertThrows(CustomException.class, () -> userService.signUp(signupDto));
    }

    @Test
    void saveProfile_CallsUserRepositorySaveMethod() {
        // Arrange
        User user = new User();

        // Act
        userService.saveProfile(user);

        // Assert
        verify(userRepository).save(user);
    }

    @Test
    void getUser_ExistingEmail_ReturnsUser() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Act
        User result = userService.getUser(email);

        // Assert
        assertEquals(user, result);
    }

    @Test
    void getUser_NonExistingEmail_ReturnsNull() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act
        User result = userService.getUser(email);

        // Assert
        assertNull(result);
    }

    @Test
    void getUser_ReturnsUserDto() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("1234567890");
        user.setRole(Role.BORROWER);

        // Act
        com.nineleaps.leaps.dto.user.UserDto userDto = userService.getUser(user);

        // Assert
        assertNotNull(userDto);
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getPhoneNumber(), userDto.getPhoneNumber());
        assertEquals(user.getRole(), userDto.getRole());
    }

    @Test
    void getGuest_GuestUserExists_ReturnsGuestUser() {
        // Arrange
        User guestUser = new User();
        when(userRepository.findByRole(Role.GUEST)).thenReturn(guestUser);

        // Act
        User result = userService.getGuest();

        // Assert
        assertEquals(guestUser, result);
    }

    @Test
    void getGuest_GuestUserDoesNotExist_ReturnsNull() {
        // Arrange
        when(userRepository.findByRole(Role.GUEST)).thenReturn(null);

        // Act
        User result = userService.getGuest();

        // Assert
        assertNull(result);
    }

    @Test
    void updateProfile_CallsUserRepositorySaveMethod() {
        // Arrange
        User oldUser = new User();
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();

        // Act
        userService.updateProfile(oldUser, profileUpdateDto);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateProfileImage_UpdatesProfileImageUrlAndCallsUserRepositorySaveMethod() {
        // Arrange
        String profileImageUrl = "https://example.com/profile.jpg";
        User user = new User();

        // Act
        userService.updateProfileImage(profileImageUrl, user);

        // Assert
        assertEquals(profileImageUrl, user.getProfileImageUrl());
        verify(userRepository).save(user);
    }

    @Test
    void getUserViaPhoneNumber_ExistingPhoneNumber_ReturnsUser() {
        // Arrange
        String phoneNumber = "1234567890";
        User user = new User();
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(user);

        // Act
        User result = userService.getUserViaPhoneNumber(phoneNumber);

        // Assert
        assertEquals(user, result);
    }

    @Test
    void getUserViaPhoneNumber_NonExistingPhoneNumber_ReturnsNull() {
        // Arrange
        String phoneNumber = "1234567890";
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(null);

        // Act
        User result = userService.getUserViaPhoneNumber(phoneNumber);

        // Assert
        assertNull(result);
    }

//    @Test
//    void getUsers_ReturnsAllUsers() {
//        // Arrange
//        List<User> userList = new ArrayList<>();
//        userList.add(new User());
//        userList.add(new User());
//        when(userRepository.findAll()).thenReturn(userList);
//
//        // Act
//        List<User> result = userService.getUsers();
//
//        // Assert
//        assertEquals(userList, result);
//    }
}
