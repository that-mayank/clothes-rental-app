package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.LoginDto;
import com.nineleaps.leaps.dto.user.LoginResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.model.User;

public interface UserServiceInterface {
    ResponseDto signUp(SignupDto signupDto);

    LoginResponseDto login(LoginDto loginDto);

    void saveProfile(User user);

    User getGuest();

    void updateProfile(User oldUser, ProfileUpdateDto profileUpdateDto);

    void updateProfileImage(String profileImageUrl, User user);

    void deleteProfileImage(User user);
}
