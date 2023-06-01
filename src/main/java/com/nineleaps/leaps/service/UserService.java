package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.enums.ResponseStatus;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.utils.Helper;
import lombok.RequiredArgsConstructor;
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

import static com.nineleaps.leaps.LeapsProductionApplication.ngrok_url;
import static com.nineleaps.leaps.config.MessageStrings.USER_CREATED;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService implements UserServiceInterface, UserDetailsService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("user not found in the database");
        } else {
            log.info("user found in the database: {}", email);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public ResponseDto signUp(SignupDto signupDto) throws CustomException {
        //Check if the current email has already been registered. i.e. User already exists
        if (Helper.notNull(userRepository.findByEmail(signupDto.getEmail()))) {
            //if email already registered throw custom exception
            throw new CustomException("Email already associated with other user");
        }
        //add for phone number
        if(Helper.notNull(userRepository.findByPhoneNumber(signupDto.getPhoneNumber()))) {
            throw new CustomException("Phone number already associated with other user");
        }
        //encrypting the password
        String encryptedPassword = passwordEncoder.encode(signupDto.getPassword());
        User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(), signupDto.getPhoneNumber(), encryptedPassword, signupDto.getRole());
        try {
            userRepository.save(user);
            return new ResponseDto(ResponseStatus.success.toString(), USER_CREATED);
        } catch (Exception e) {
            //handle signup error
            throw new CustomException(e.getMessage());
        }
    }


    @Override
    public void saveProfile(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        log.info("getting user{} from the database", userId);
        return userRepository.findById(userId).get();

    }

    @Override
    public User getUser(String email) {
        log.info("getting user{} from the database", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDto getUser(User user) {
        return new UserDto(user);
    }

    @Override
    public User getGuest() {
        User user = userRepository.findByRole(Role.guest);
        if (!Helper.notNull(user)) {
            return null; //create a guest user call that function
        } else {
            return user;
        }
    }

    @Override
    public void updateProfile(User oldUser, ProfileUpdateDto profileUpdateDto) {
        User user = new User(profileUpdateDto, oldUser);
        userRepository.save(user);
    }

    @Override
    public void updateProfileImage(String profileImageUrl, User user) {
//if url does not contain ngrok url, directly save it
        String imageUrl = profileImageUrl;
//remove ngrok link
        if (profileImageUrl.contains(ngrok_url)) {
            imageUrl = profileImageUrl.substring(ngrok_url.length());
        }
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
    }

    @Override
    public User getUserViaPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);

    }

    @Override
    public List<User> getUsers() {
        log.info("getting all user from the database");

        return userRepository.findAll();
    }
}
