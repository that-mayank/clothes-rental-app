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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@Validated
@Api(tags = "User Api")

public class UserController {

    //Linking layers using constructor injection

    private final UserServiceInterface userServiceInterface;
    private final SwitchProfile switchprofile;
    private final Helper helper;
    private final SecurityUtility securityUtility;
    private final RefreshTokenServiceInterface refreshTokenService;

    // API : For user registration

    @ApiOperation(value = "API : For user registration")
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)

    public ResponseDto signup(@RequestBody SignupDto signupDto) throws CustomException {
        return userServiceInterface.signUp(signupDto);
    }

    // API : Admin functionality to get all the users
    @ApiOperation(value = "API : To get all the users")
    @GetMapping(value = "/getAllUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userServiceInterface.getUsers(), HttpStatus.OK);
    }

    // API : To switch between owner and borrower

    @ApiOperation(value = "API : To switch between owner and borrower")
    @PostMapping(value = "/switch", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)

    // only owner and borrower can access this

    public ResponseEntity<ApiResponse> switchProfile(@RequestParam(value = "role", required = true) Role profile, HttpServletResponse response, HttpServletRequest request) throws AuthenticationFailException, UserNotExistException, IOException {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        if (!Helper.notNull(user)) {
            throw new UserNotExistException("User is invalid");
        }

        // Guard statement : To  verify role should be owner or borrower

        if (profile == Role.OWNER || profile == Role.BORROWER) {
            user.setRole(profile);
            userServiceInterface.saveProfile(user);
            switchprofile.generateTokenForSwitchProfile(response, profile, request);

            return new ResponseEntity<>(new ApiResponse(true, "Role switch to: " + user.getRole()), HttpStatus.OK);
        } else  {

            // Handle the case when 'profile' parameter is not provided

            return new ResponseEntity<>(new ApiResponse(false, "Missing 'profile' parameter"), HttpStatus.BAD_REQUEST);
        }
    }

    // API : To update profile of user
    // only owner and borrower can access this

    @ApiOperation(value = "API : To update user profile")
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)

    public ResponseEntity<ApiResponse> updateProfile(@RequestBody @Valid ProfileUpdateDto profileUpdateDto, HttpServletRequest request) throws AuthenticationFailException {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User oldUser = helper.getUser(token);

        if (!Helper.notNull(oldUser)) {
            return new ResponseEntity<>(new ApiResponse(false, "User not found"), HttpStatus.NOT_FOUND);
        }

        // Calling service layer to update the user

        userServiceInterface.updateProfile(oldUser, profileUpdateDto);
        return new ResponseEntity<>(new ApiResponse(true, "Profile updated successfully"), HttpStatus.OK);
    }

    // API : To get the current user

    @ApiOperation(value = "API : To get the current user")
    @GetMapping(value = "/getUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)

    public ResponseEntity<UserDto> getUser(HttpServletRequest request) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        // Calling service layer to return current user

        UserDto userDto = userServiceInterface.getUser(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    // API : To update and add user profile picture

    @ApiOperation(value = "API : To update and add user profile picture")
    @PostMapping(value = "/updateProfilePicture", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)

    public ResponseEntity<ApiResponse> profileImage(@RequestParam(value = "profileImageUrl") String profileImageUrl, HttpServletRequest request) throws AuthenticationFailException {

        // Handle the case when 'profileImageUrl' parameter is not provided

        if (profileImageUrl == null || profileImageUrl.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Profile image parameter is missing"), HttpStatus.BAD_REQUEST);
        }

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        if (!Helper.notNull(user)) {
            return new ResponseEntity<>(new ApiResponse(false, "User is invalid"), HttpStatus.NOT_FOUND);
        }

        // Calling service layer to save or update profile picture

        userServiceInterface.updateProfileImage(profileImageUrl, user);
        return new ResponseEntity<>(new ApiResponse(true, "Profile picture has been updated."), HttpStatus.CREATED);
    }

    // API : To update and add new access token

    @ApiOperation(value = "API : To update and add new access token")
    @PostMapping(value = "/refreshToken", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)

    public ResponseEntity<ApiResponse> updateTokenUsingRefreshToken(HttpServletRequest request, HttpServletResponse response) throws AuthenticationFailException, IOException {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);

        // Checking if refresh token is valid

        if (securityUtility.isRefreshTokenExpired(token)) {
            User user = helper.getUser(token);
            String email = user.getEmail();
            String newAccessToken = securityUtility.updateAccessTokenViaRefreshToken(email, request, token);
            response.setHeader("access_token", newAccessToken);

            return new ResponseEntity<>(new ApiResponse(true, "AccessToken Updated Via RefreshToken"), HttpStatus.CREATED);

            // if refresh token is invalid

        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "RefreshToken expired");

            return new ResponseEntity<>(new ApiResponse(false, "RefreshToken Expired , Login Again"), HttpStatus.UNAUTHORIZED);
        }
    }

    // API : To log out the current user

    @ApiOperation(value = "API : To log out the current user")
    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)

    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        // Extracting email from user

        String email = user.getEmail();

        // Making refresh token invalid

        refreshTokenService.deleteRefreshTokenByEmailAndToken(email, token);

        return new ResponseEntity<>(new ApiResponse(true, "User Successfully Logged out "), HttpStatus.CREATED);
    }
}


