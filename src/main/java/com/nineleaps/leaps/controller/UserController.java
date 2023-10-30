package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.UserNotExistException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.RefreshTokenServiceInterface;
import com.nineleaps.leaps.service.UserServiceInterface;

import com.nineleaps.leaps.utils.Helper;
import com.nineleaps.leaps.utils.SecurityUtility;
import com.nineleaps.leaps.utils.SwitchProfile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


import static com.nineleaps.leaps.config.MessageStrings.AUTH_TOKEN_NOT_VALID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@Api(tags = "User Api", description = "Contains api for user onboarding and actions")
@SuppressWarnings("deprecation")
@Slf4j
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

        try {
            // Calling userServiceInterface to do the signup process
            userServiceInterface.signUp(signupDto);

            log.info("User signed up successfully: {}", signupDto.getEmail());

            // Status Code - 201-HttpStatus.CREATED
            return new ResponseEntity<>(new ApiResponse(true, "Signed up successfully"), HttpStatus.CREATED);
        } catch (CustomException e) {
            log.error("Error during user signup: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Signup failed"));
        }
    }




    // API: Functionality to Switch Between Borrower and Owner
    @ApiOperation(value = "To switch between owner and borrower")
    @PostMapping(value = "/switch")
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> switchProfile(@RequestParam Role profile, HttpServletResponse response, HttpServletRequest request) throws AuthenticationFailException, UserNotExistException, IOException {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            if (!Helper.notNull(user)) {
                log.error(AUTH_TOKEN_NOT_VALID);
                throw new UserNotExistException(AUTH_TOKEN_NOT_VALID);
            }

            user.setRole(profile);

            // Calling the service layer to save the profile of the user
            userServiceInterface.saveProfile(user);

            // Calling switch profile utility file to generate new AccessTokens for the newly switched profile
            switchprofile.generateTokenForSwitchProfile(response, profile, request);

            log.info("User role switched to: {}", user.getRole());

            // Status code - 200-HttpStatus.OK
            return new ResponseEntity<>(new ApiResponse(true, "Role switched to: " + user.getRole()), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while switching profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error switching profile"));
        }
    }

    // API - Helps user to update his profile information
    @ApiOperation(value = "Api to update user profile")
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody @Valid ProfileUpdateDto profileUpdateDto, HttpServletRequest request) throws AuthenticationFailException {

        try {
            // Extract User from the token
            User oldUser = helper.getUserFromToken(request);

            if (!Helper.notNull(oldUser)) {
                log.error(AUTH_TOKEN_NOT_VALID);
                // Status Code: 404-HttpStatus.NOT_FOUND
                return new ResponseEntity<>(new ApiResponse(false, AUTH_TOKEN_NOT_VALID), HttpStatus.NOT_FOUND);
            }

            // Interact with the service layer to update the profile
            userServiceInterface.updateProfile(oldUser, profileUpdateDto);
            log.info("Profile updated successfully for user: {}", oldUser.getEmail());

            // Status Code: 200-HttpStatus.Ok
            return new ResponseEntity<>(new ApiResponse(true, "Profile updated successfully"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while updating profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error updating profile"));
        }
    }

    // API - Gives details about the currently logged-in user
    @ApiOperation(value = "Api to get current user")
    @GetMapping(value = "/getUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('OWNER','BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    public ResponseEntity<UserDto> getUser(HttpServletRequest request) {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            // Check if the user is null
            if (!Helper.notNull(user)) {
                log.error("User not found");
                // Status Code: 404-HttpStatus.NOT_FOUND
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

            // Calling the service layer to get user details
            UserDto userDto = userServiceInterface.getUser(user);
            log.info("User details fetched successfully for user: {}", user.getEmail());

            // Status Code: 200-HttpStatus.OK
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while getting user details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // API - Allows the User to Update his profile picture
    @ApiOperation(value = "Api to update and add user profile picture")
    @PostMapping(value = "/updateProfilePicture",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')") // Adding Method Level Authorization Via RBAC-Role-Based Access Control
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> profileImage(@RequestParam("profileImageUrl") String profileImageUrl, HttpServletRequest request) throws AuthenticationFailException {

        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            // Check if the user is null
            if (!Helper.notNull(user)) {
                log.error(AUTH_TOKEN_NOT_VALID);
                // Status Code: 404-HttpStatus.NOT_FOUND
                return new ResponseEntity<>(new ApiResponse(false, AUTH_TOKEN_NOT_VALID), HttpStatus.NOT_FOUND);
            }

            // Calling the Service Layer to update the profile picture
            userServiceInterface.updateProfileImage(profileImageUrl, user);
            log.info("Profile picture updated successfully for user: {}", user.getEmail());

            // Status Code: 201-HttpStatus.CREATED
            return new ResponseEntity<>(new ApiResponse(true, "Profile picture has been updated."), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error while updating profile picture", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error updating profile picture"));
        }
    }


    // API - Allows the user to generate a new Access token using refresh token when the access token got expired
    @ApiOperation(value = "Api to update and add new access token")
    @PostMapping(value = "/refreshToken")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> updateTokenUsingRefreshToken(HttpServletRequest request,HttpServletResponse response) throws AuthenticationFailException, IOException {

        try {
            // Fetch token from the header
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            String token = authorizationHeader.substring(7);

            // Extract user from the token
            User user = helper.getUser(token);
            String email = user.getEmail();

            // Calling Security utility file to generate a new token
            String accessToken = securityUtility.updateAccessTokenViaRefreshToken(email, request, token);

            // Set the newly generated token to its respective header
            response.setHeader("access_token", accessToken);
            log.info("AccessToken updated via RefreshToken for user: {}", user.getEmail());

            // Status Code: 201-HttpStatus.CREATED
            return new ResponseEntity<>(new ApiResponse(true, "AccessToken Updated Via RefreshToken"), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error while updating access token via RefreshToken", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error updating access token"));
        }
    }


    // API - Allows the user to Log out
    @ApiOperation(value="Api to Logout")
    @PostMapping(value = "/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request){

        try {
            // Fetch token from the header
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            String token = authorizationHeader.substring(7);

            // Extract user from the token
            User user = helper.getUser(token);
            String email = user.getEmail();

            // Calling the service layer to delete the refresh token in the DB during logout.
            refreshTokenService.deleteRefreshTokenByEmailAndToken(email, token);
            helper.setUserToBorrower(request);
            log.info("User logged out successfully: {}", user.getEmail());

            // Status Code: 200-HttpStatus.OK
            return new ResponseEntity<>(new ApiResponse(true, "User Successfully Logged out"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during user logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error logging out"));
        }
    }



}


