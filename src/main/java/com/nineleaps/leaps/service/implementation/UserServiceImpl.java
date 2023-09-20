package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.enums.ResponseStatus;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static com.nineleaps.leaps.config.MessageStrings.USER_CREATED;
import static com.nineleaps.leaps.config.MessageStrings.USER_NOT_FOUND;

@Service // Marks this class as a Spring service component
@Slf4j // Lombok's annotation to generate a logger for this class
@AllArgsConstructor // Lombok's annotation to generate a constructor with all required fields
@Transactional // Marks this class as transactional for database operations
public class UserServiceImpl implements UserServiceInterface, UserDetailsService {

    private final UserRepository userRepository; // Injects the UserRepository
    private final PasswordEncoder passwordEncoder; // Injects the PasswordEncoder

    // Implementation of the UserDetailsService interface to load user details by username (email).
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.error(USER_NOT_FOUND);
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        } else {
            log.info("user found in the database: {}", email);
        }

        // Create authorities for the user based on their role.
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

        // Return a UserDetails object with user information and authorities.
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    // Save the device token to a user's profile.
    @Override
    public void saveDeviceTokenToUser(String email, String deviceToken) {
        // Find the user by their email address.
        User user = userRepository.findByEmail(email);

        // Check if the user exists in the database.
        if (user == null) {
            log.error(USER_NOT_FOUND);
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        } else {
            // Find the user's existing device token, if any.
            User existingDeviceToken = userRepository.findDeviceTokenByEmail(email);

            // Set the new device token for the user.
            user.setDeviceToken(deviceToken);

            // Save the updated user profile to the database.
            userRepository.save(user);

            // Log information about the device token update or save.
            if (existingDeviceToken != null) {
                log.info("Device token updated for user: {} and token is: {}", email, deviceToken);
            } else {
                log.info("Device token saved for user: {}", email);
            }
        }
    }


    // Sign up a new user.
    @Override
    public ResponseDto signUp(SignupDto signupDto) throws CustomException {
        // Check if the provided email has already been registered.
        if (Helper.notNull(userRepository.findByEmail(signupDto.getEmail()))) {
            // If email is already registered, throw a custom exception.
            throw new CustomException("Email already associated with another user");
        }

        // Check if the provided phone number is already registered.
        if (Helper.notNull(userRepository.findByPhoneNumber(signupDto.getPhoneNumber()))) {
            throw new CustomException("Phone number already associated with another user");
        }

        // Encrypt the user's password.
        String encryptedPassword = passwordEncoder.encode(signupDto.getPassword());

        // Create a new user object with the provided details.
        User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(),
                signupDto.getPhoneNumber(), encryptedPassword, signupDto.getRole());

        try {
            // Save the user to the database.
            userRepository.save(user);
            return new ResponseDto(ResponseStatus.SUCCESS.toString(), USER_CREATED);
        } catch (Exception e) {
            // Handle signup error and throw a custom exception with the error message.
            throw new CustomException(e.getMessage());
        }
    }

    // Save user profile information.
    @Override
    public void saveProfile(User user) {
        userRepository.save(user);
    }

    // Get a user by email.
    @Override
    public User getUser(String email) {
        log.info("getting user {} from the database", email);
        return userRepository.findByEmail(email);
    }

    // Get a UserDto object from a User.
    @Override
    public UserDto getUser(User user) {
        return new UserDto(user);
    }

    // Get a guest user or create one if it doesn't exist.
    @Override
    public User getGuest() {
        User user = userRepository.findByRole(Role.GUEST);
        if (!Helper.notNull(user)) {
            return null; // Create a guest user if not found.
        } else {
            return user;
        }
    }

    // Update user profile information.
    @Override
    public void updateProfile(User oldUser, ProfileUpdateDto profileUpdateDto) {
        User user = new User(profileUpdateDto, oldUser);
        userRepository.save(user);
    }

    // Update user profile image.
    @Override
    public void updateProfileImage(String profileImageUrl, User user) {
        // If the URL does not contain the ngrok URL, save it directly.
        String imageUrl = profileImageUrl;

        // Remove ngrok link from the image URL.
        if (profileImageUrl.contains(NGROK)) {
            imageUrl = profileImageUrl.substring(NGROK.length());
        }

        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
    }

    // Get a user by phone number.
    @Override
    public User getUserViaPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    // Get a list of all users from the database.
    @Override
    public List<User> getUsers() {
        log.info("getting all users from the database");
        return userRepository.findAll();
    }
}
