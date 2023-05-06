package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.LoginDto;
import com.nineleaps.leaps.dto.user.LoginResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceInterface userService;
    private final AuthenticationServiceInterface authenticationService;


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
        User user;
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

    //update profile
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateProfile(@RequestParam("token") String token, @RequestBody ProfileUpdateDto profileUpdateDto) throws AuthenticationFailException {
        //authenticate token
        authenticationService.authenticate(token);
        //retrieve user
        User oldUser = authenticationService.getUser(token);
        if (!Helper.notNull(oldUser)) {
            return new ResponseEntity<>(new ApiResponse(false, "User not found"), HttpStatus.NOT_FOUND);
        }
        //update the user
        userService.updateProfile(oldUser, profileUpdateDto);
        return new ResponseEntity<>(new ApiResponse(true, "Profile updated successfully"), HttpStatus.OK);
    }
    //update password method to be declared

    //profile picture update api
    @PostMapping("/updateProfilePicture")
    public ResponseEntity<ApiResponse> profileImage(@RequestParam("token") String token, @RequestParam("profileImageUrl") String profileImageUrl) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        //check if user is valid or not
        User user = authenticationService.getUser(token);
        if (!Helper.notNull(user)) {
            return new ResponseEntity<>(new ApiResponse(false, "User is invalid"), HttpStatus.NOT_FOUND);
        }
        //check if image url is not null
        if (!Helper.notNull(profileImageUrl)) {
            return new ResponseEntity<>(new ApiResponse(false, "Profile image url is empty."), HttpStatus.BAD_REQUEST);
        }
        userService.updateProfileImage(profileImageUrl, user);
        return new ResponseEntity<>(new ApiResponse(true, "Profile picture has been updated."), HttpStatus.CREATED);
    }

    @GetMapping("/getUser")
    public ResponseEntity<User> getUser(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/deleteProfilePicture")
    public ResponseEntity<ApiResponse> deleteProfileImage(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        if (!Helper.notNull(user)) {
            return new ResponseEntity<>(new ApiResponse(false, "User is invalid!"), HttpStatus.NOT_FOUND);
        }
        userService.deleteProfileImage(user);
        return new ResponseEntity<>(new ApiResponse(true, "Profile picture deleted successfully."), HttpStatus.OK);
    }
}
