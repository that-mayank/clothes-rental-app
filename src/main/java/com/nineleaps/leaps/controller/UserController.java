package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.LoginDto;
import com.nineleaps.leaps.dto.user.LoginResponseDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.UserNotExistException;
import com.nineleaps.leaps.model.Guest;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.AuthenticationServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserServiceInterface userService;
    private final AuthenticationServiceInterface authenticationService;

    @Autowired
    public UserController(UserServiceInterface userService, AuthenticationServiceInterface authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody SignupDto signupDto) throws CustomException {
        return userService.signUp(signupDto);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginDto loginDto) throws CustomException {
        return userService.login(loginDto);
    }

    @PostMapping("/switch")
    public ResponseEntity<ApiResponse> switchProfile(@RequestParam Role profile, @RequestParam(required = false) String token) throws AuthenticationFailException, UserNotExistException {
        User user = null;
        if (profile == Role.guest) {
            user = userService.getGuest();
            if (!Helper.notNull(user)) {
                user = new Guest();
                userService.saveProfile(user);
            }
        } else {
            authenticationService.authenticate(token);
            user = authenticationService.getUser(token);
            if (!Helper.notNull(user)) {
                throw new UserNotExistException("User is invalid");
            }
            user.setRole(profile);
            userService.saveProfile(user);
        }
        return new ResponseEntity<>(new ApiResponse(true, "Role switch to: " + user.getRole()), HttpStatus.OK);
    }
}
