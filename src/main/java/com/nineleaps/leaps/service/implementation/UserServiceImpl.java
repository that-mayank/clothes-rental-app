package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.enums.ResponseStatus;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserLoginInfo;
import com.nineleaps.leaps.repository.UserLoginInfoRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static com.nineleaps.leaps.config.MessageStrings.USER_CREATED;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserServiceInterface, UserDetailsService {
    private final UserRepository userRepository;
private final UserLoginInfoRepository userLoginInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Bad Credentials");
        } else {
            log.info("user found in the database: {}", email);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public void saveDeviceTokenToUser(String email, String deviceToken) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Bad Credentials");
        } else {
            User existingDeviceToken = userRepository.findDeviceTokenByEmail(email);

            if (existingDeviceToken != null) {
                user.setDeviceToken(deviceToken);
                user.setAuditColumnsCreate(user);
                user.setAuditColumnsUpdate(user.getId());
                userRepository.save(user);
                log.info("Device token updated for user: {} and token is: {}", email, deviceToken);
            } else {
                user.setDeviceToken(deviceToken);
                user.setAuditColumnsCreate(user);
                user.setAuditColumnsUpdate(user.getId());
                userRepository.save(user);
                log.info("Device token saved for user: {}", email);
            }
        }
    }


    @Override
    public void signUp(SignupDto signupDto) throws CustomException {
        //Check if the current email has already been registered. I.e. User already exists
        if (Helper.notNull(userRepository.findByEmail(signupDto.getEmail()))) {
            //if email already registered throws custom exception
            throw new CustomException("Email already associated with other user");
        }
        //add exception for registered phone number
        if (Helper.notNull(userRepository.findByPhoneNumber(signupDto.getPhoneNumber()))) {
            throw new CustomException("Phone number already associated with other user");
        }
        //encrypting the password
        String encryptedPassword = passwordEncoder.encode(signupDto.getPassword());
        User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(), signupDto.getPhoneNumber(), encryptedPassword, signupDto.getRole());
        try {
            user.setCreatedAt(LocalDateTime.now());
            user.setCreatedBy(user.getId());
            user.setAuditColumnsUpdate(user.getId());
            userRepository.save(user);
            UserLoginInfo userLoginInfo = new UserLoginInfo();
            userLoginInfo.initializeLoginInfo(user);
            userLoginInfoRepository.save(userLoginInfo);
            new ResponseDto(ResponseStatus.SUCCESS.toString(), USER_CREATED);
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
        User user = userRepository.findByRole(Role.GUEST);
        if (!Helper.notNull(user)) {
            return null; //create a guest user call that function
        } else {
            return user;
        }
    }

    @Override
    public void updateProfile(User oldUser, ProfileUpdateDto profileUpdateDto) {
        User user = new User(profileUpdateDto, oldUser);
        user.setAuditColumnsCreate(oldUser);
        user.setAuditColumnsUpdate(user.getId());
        userRepository.save(user);
    }

    @Override
    public void updateProfileImage(String profileImageUrl, User user) {
//if url does not contain ngrok url, directly save it
        String imageUrl = profileImageUrl;
//remove ngrok link
        if (profileImageUrl.contains(NGROK)) {
            imageUrl = profileImageUrl.substring(NGROK.length());
        }
        user.setProfileImageUrl(imageUrl);
        user.setAuditColumnsCreate(user);
        user.setAuditColumnsUpdate(user.getId());
        userRepository.save(user);
    }

    @Override
    public User getUserViaPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);

    }

    @Override
    public void getUsers() {
        log.info("getting all user from the database");
        userRepository.findAll();
    }
}
