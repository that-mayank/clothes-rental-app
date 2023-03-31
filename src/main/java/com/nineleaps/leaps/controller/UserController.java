package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.LoginDto;
import com.nineleaps.leaps.dto.user.LoginResponseDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody SignupDto signupDto) throws CustomException {
        return userService.signUp(signupDto);
    }
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginDto loginDto) throws CustomException {
        return userService.login(loginDto);
    }
}
