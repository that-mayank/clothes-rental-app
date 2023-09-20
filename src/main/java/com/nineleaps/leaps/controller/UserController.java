package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@Api(tags = "User Api", description = "Contains api for user onboarding and actions")
@SuppressWarnings("deprecation")
public class UserController {


    // Linking layers using constructor injection

    private final UserServiceInterface userServiceInterface;
    private final SwitchProfile switchprofile;
    private final Helper helper;
    private final SecurityUtility securityUtility;
    private final RefreshTokenServiceInterface refreshTokenService;

    // API: Allows the users to do Sign-Up
    @ApiOperation(value = "user registration api")
    @PostMapping(value = "/signup" , consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> signup(@RequestBody SignupDto signupDto) throws CustomException {

        // Calling userServiceInterface to do the signup process
        userServiceInterface.signUp(signupDto);

        // Status Code - 201-HttpStatus.CREATED
        return new ResponseEntity<>(new ApiResponse(true, "SignedUp Successfully"), HttpStatus.CREATED);
    }




    // API: Functionality to Switch Between Borrower and Owner
    @ApiOperation(value = "To switch between owner and borrower")
    @PostMapping(value = "/switch")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> switchProfile(@RequestParam Role profile, HttpServletResponse response, HttpServletRequest request) throws AuthenticationFailException, UserNotExistException, IOException {
        User user;

        // Check if the user is a guest. If not, then switch profile for the user
        if (profile == Role.GUEST) {
            user = userServiceInterface.getGuest();
            if (!Helper.notNull(user)) {
                user = new Guest();
                userServiceInterface.saveProfile(user);
            }
        } else {
            // Extract User from the token
            user = helper.getUserFromToken(request);

            if (!Helper.notNull(user)) {
                throw new UserNotExistException("User is invalid");
            }
            user.setRole(profile);

            // Calling the service layer to save profile of the user
            userServiceInterface.saveProfile(user);

            //  Calling switch profile utility file to Generate new AccessTokens for the newly Switched Profile.
            switchprofile.generateTokenForSwitchProfile(response, profile, request);
        }
        // Status code - 200-HttpStatus.OK
        return new ResponseEntity<>(new ApiResponse(true, "Role switch to: " + user.getRole()), HttpStatus.OK);
    }

    // API - Helps user to update his profile information
    @ApiOperation(value = "Api to update user profile")
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody @Valid ProfileUpdateDto profileUpdateDto, HttpServletRequest request) throws AuthenticationFailException {

        // Extract User from the token
        User oldUser = helper.getUserFromToken(request);


        // Check if user is null
        if (!Helper.notNull(oldUser)) {

            // Status Code: 404-HttpStatus.NOT_FOUND
            return new ResponseEntity<>(new ApiResponse(false, "User not found"), HttpStatus.NOT_FOUND);
        }
        // Interact with the service layer to update profile
        userServiceInterface.updateProfile(oldUser, profileUpdateDto);

        // Status Code : 200-HttpStatus.Ok
        return new ResponseEntity<>(new ApiResponse(true, "Profile updated successfully"), HttpStatus.OK);
    }

    // API - Gives details about the currently logged-in user
    @ApiOperation(value = "Api to get current user")
    @GetMapping(value = "/getUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('OWNER','BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    public ResponseEntity<UserDto> getUser(HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Calling the service layer to get user details
        UserDto userDto = userServiceInterface.getUser(user);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    // API - Allows the User to Update his profile picture
    @ApiOperation(value = "Api to update and add user profile picture")
    @PostMapping(value = "/updateProfilePicture",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> profileImage(@RequestParam("profileImageUrl") String profileImageUrl, HttpServletRequest request) throws AuthenticationFailException {

        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // check if user is null
        if (!Helper.notNull(user)) {

            // Status Code: 404-HttpStatus.NOT_FOUND
            return new ResponseEntity<>(new ApiResponse(false, "User is invalid"), HttpStatus.NOT_FOUND);
        }
        // Calling the Service Layer to update profile picture
        userServiceInterface.updateProfileImage(profileImageUrl, user);

        // Status Code : 201-HttpStatus.CREATED
        return new ResponseEntity<>(new ApiResponse(true, "Profile picture has been updated."), HttpStatus.CREATED);
    }


    // API - Allows the user to generate a new Access token using refresh token when the access token got expired
    @ApiOperation(value = "Api to update and add new access token")
    @PostMapping(value = "/refreshToken")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> updateTokenUsingRefreshToken(HttpServletRequest request,HttpServletResponse response) throws AuthenticationFailException, IOException {

        // Fetch token from header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);

            // Extract user from token
            User user = helper.getUser(token);
            String email = user.getEmail();

            // Calling Security utility file to generate a new token
            String accessToken = securityUtility.updateAccessTokenViaRefreshToken(email,request,token);

            // set the newly generated token to its respective header
            response.setHeader("access_token",accessToken);

            // Status Code : 201-HttpStatus.CREATED
            return new ResponseEntity<>(new ApiResponse(true, "AccessToken Updated Via RefreshToken"), HttpStatus.CREATED);
    }


    // API - Allows the user to Log out
    @ApiOperation(value="Api to Logout")
    @PostMapping(value = "/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request){

        // Fetch token from header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);

        // Extract user from token
        User user = helper.getUser(token);
        String email = user.getEmail();

        // Calling the service layer to delete the refresh token in DB during logout.
        refreshTokenService.deleteRefreshTokenByEmailAndToken(email,token);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(new ApiResponse(true, "User Successfully Logged out "), HttpStatus.OK);
    }



}


