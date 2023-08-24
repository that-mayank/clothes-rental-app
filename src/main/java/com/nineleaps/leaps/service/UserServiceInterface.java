package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.model.User;

import java.util.List;

public interface UserServiceInterface {
    ResponseDto signUp(SignupDto signupDto);


    List<User> getUsers();

    void saveProfile(User user);

    User getUser(String email);

    UserDto getUser(User user);

    User getGuest();

    User getUserViaPhoneNumber(String phoneNumber);

    void updateProfile(User oldUser, ProfileUpdateDto profileUpdateDto);

    void updateProfileImage(String profileImageUrl, User user);

    void saveDeviceTokenToUser(String email,String deviceToken);


}
