package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.model.User;

import java.util.List;
import java.util.Optional;

public interface UserServiceInterface {
    public ResponseDto signUp(SignupDto signupDto);


    List<User> getUsers();

    public void saveProfile(User user);

    public User getUserById(Long userId);

    public User getUser(String email);

    UserDto getUser(User user);

    public User getGuest();

    User getUserViaPhoneNumber(String phoneNumber);

    public void updateProfile(User oldUser, ProfileUpdateDto profileUpdateDto);

    void updateProfileImage(String profileImageUrl, User user);
}
