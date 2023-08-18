package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.UserNotExistException;
import com.nineleaps.leaps.model.Guest;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.RefreshTokenServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import com.nineleaps.leaps.utils.SecurityUtility;
import com.nineleaps.leaps.utils.SwitchProfile;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@Api(tags = "User Api", description = "Contains api for user onboarding")
@SuppressWarnings("deprecation")
public class UserController {
    private final UserServiceInterface userServiceInterface;
    private final SwitchProfile switchprofile;
    private final Helper helper;
    private final SecurityUtility securityUtility;
    private final RefreshTokenServiceInterface refreshTokenService;


    @ApiOperation(value = "user registration api")
    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody SignupDto signupDto) throws CustomException {
        return userServiceInterface.signUp(signupDto);
    }

    // admin functionality to get all the users
    @ApiOperation(value = "Api to get all the users")
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok().body(userServiceInterface.getUsers());
    }


    @ApiOperation(value = "To switch between owner and borrower")
    @PostMapping("/switch")
    public ResponseEntity<ApiResponse> switchProfile(@RequestParam Role profile, HttpServletResponse response, HttpServletRequest request) throws AuthenticationFailException, UserNotExistException, IOException {
        User user;
        if (profile == Role.GUEST) {
            user = userServiceInterface.getGuest();
            if (!Helper.notNull(user)) {
                user = new Guest();
                userServiceInterface.saveProfile(user);
            }
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            String token = authorizationHeader.substring(7);
            user = helper.getUser(token);
            if (!Helper.notNull(user)) {
                throw new UserNotExistException("User is invalid");
            }
            user.setRole(profile);
            userServiceInterface.saveProfile(user);
            switchprofile.generateTokenForSwitchProfile(response, profile, request);
        }
        return new ResponseEntity<>(new ApiResponse(true, "Role switch to: " + user.getRole()), HttpStatus.OK);
    }

    //update profile
    @ApiOperation(value = "Api to update user profile")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody ProfileUpdateDto profileUpdateDto, HttpServletRequest request) throws AuthenticationFailException {


        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User oldUser = helper.getUser(token);

        if (!Helper.notNull(oldUser)) {
            return new ResponseEntity<>(new ApiResponse(false, "User not found"), HttpStatus.NOT_FOUND);
        }
        //update the user
        userServiceInterface.updateProfile(oldUser, profileUpdateDto);
        return new ResponseEntity<>(new ApiResponse(true, "Profile updated successfully"), HttpStatus.OK);
    }

    // to get the current user
    @ApiOperation(value = "Api to get current user")
    @GetMapping("/getUser")
    public ResponseEntity<UserDto> getUser(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        UserDto userDto = userServiceInterface.getUser(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @ApiOperation(value = "Api to update and add user profile picture")
    @PostMapping("/updateProfilePicture")
    public ResponseEntity<ApiResponse> profileImage(@RequestParam("profileImageUrl") String profileImageUrl, HttpServletRequest request) throws AuthenticationFailException {
        //check if user is valid or not
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        if (!Helper.notNull(user)) {
            return new ResponseEntity<>(new ApiResponse(false, "User is invalid"), HttpStatus.NOT_FOUND);
        }
        userServiceInterface.updateProfileImage(profileImageUrl, user);
        return new ResponseEntity<>(new ApiResponse(true, "Profile picture has been updated."), HttpStatus.CREATED);
    }
    @ApiOperation(value = "Api to update and add new access token")
    @PostMapping("/refreshToken")
    public ResponseEntity<ApiResponse> updateTokenUsingRefreshToken(HttpServletRequest request,HttpServletResponse response) throws AuthenticationFailException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        if(!securityUtility.isRefreshTokenExpired(token)){
            User user = helper.getUser(token);
            String email = user.getEmail();
            String new_access_token = securityUtility.updateAccessTokenViaRefreshToken(email,request,response,token);
            response.setHeader("access_token",new_access_token);
            return new ResponseEntity<>(new ApiResponse(true, "AccessToken Updated Via RefreshToken"), HttpStatus.CREATED);
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED,"Refreshtoken token expired");
            return new ResponseEntity<>(new ApiResponse(false, "RefreshToken Expired , Login Again"), HttpStatus.UNAUTHORIZED);
        }

    }
    @ApiOperation(value="Api to Logout")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request){
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        String email = user.getEmail();
        refreshTokenService.deleteRefreshTokenByEmailAndToken(email,token);
        return new ResponseEntity<>(new ApiResponse(true, "User Successfully Logged out "), HttpStatus.CREATED);
    }
}


