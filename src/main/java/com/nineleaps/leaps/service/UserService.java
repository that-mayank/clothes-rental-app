package com.nineleaps.leaps.service;

import com.nineleaps.leaps.config.MessageStrings;
import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.LoginDto;
import com.nineleaps.leaps.dto.user.LoginResponseDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.enums.ResponseStatus;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.AuthenticationToken;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.nineleaps.leaps.config.MessageStrings.USER_CREATED;

@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    public ResponseDto signUp(SignupDto signupDto) throws CustomException {
        //Check if the current email has already been registered. i.e. User already exists
        if (Helper.notNull(userRepository.findByEmail(signupDto.getEmail()))) {
            //if email already registered throw custom exception
            throw new CustomException("User already exists");
        }
        //encrypting the password
        String encryptedPassword = signupDto.getPassword();
        try {
            encryptedPassword = hashPassword(encryptedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("Hashing password failed: {}", e.getMessage());
        }
        User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(), signupDto.getPhoneNumber(), encryptedPassword, signupDto.getRole());

        User createdUser;
        try {
            //save the user
            createdUser = userRepository.save(user);
            //generate token for user
            final AuthenticationToken authenticationToken = new AuthenticationToken(createdUser);
            //save the token in database
            authenticationService.saveToken(authenticationToken);
            //success in creating token and saving token
            return new ResponseDto(ResponseStatus.success.toString(), USER_CREATED);
        } catch (Exception e) {
            //handle signup error
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public LoginResponseDto login(LoginDto loginDto) throws AuthenticationFailException {
        //find user by email
        User user = userRepository.findByEmail(loginDto.getEmail());
        //user not found
        if (!Helper.notNull(user)) {
            throw new AuthenticationFailException("user not present");
        }
        try {
            //if password does not match
            if (!user.getPassword().equals(hashPassword(loginDto.getPassword()))) {
                throw new AuthenticationFailException(MessageStrings.WRONG_PASSWORD);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("Hashing password failed: {}", e.getMessage());
        }
        //if password matches get token
        AuthenticationToken token = authenticationService.getToken(user);
        //if token not found
        if (!Helper.notNull(token)) {
            throw new CustomException("Token not present");
        }
        return new LoginResponseDto(ResponseStatus.success.toString(), token.getToken());
    }

    String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }

    @Override
    public void saveProfile(User user) {
        userRepository.save(user);
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
}
