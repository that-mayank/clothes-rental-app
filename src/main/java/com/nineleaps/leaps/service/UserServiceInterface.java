package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.LoginDto;
import com.nineleaps.leaps.dto.user.LoginResponseDto;
import com.nineleaps.leaps.dto.user.SignupDto;

public interface UserServiceInterface {
    public ResponseDto signUp(SignupDto signupDto);

    public LoginResponseDto login(LoginDto loginDto);
}
