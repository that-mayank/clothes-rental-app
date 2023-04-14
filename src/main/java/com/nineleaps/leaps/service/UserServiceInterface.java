package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.LoginDto;
import com.nineleaps.leaps.dto.user.LoginResponseDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.model.User;

public interface UserServiceInterface {
    public ResponseDto signUp(SignupDto signupDto);

    public LoginResponseDto login(LoginDto loginDto);

    public void saveProfile(User user);

    public User getGuest();
}
