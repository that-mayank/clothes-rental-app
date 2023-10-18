package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.enums.Role;
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
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // API : For user registration *Tested*
    @ApiOperation(value = "API : For user registration")
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto signup(
            @RequestBody SignupDto signupDto
    ) throws CustomException {
        return userServiceInterface.signUp(signupDto);
    }

    // API : Admin functionality to get all the users *Tested*
    @ApiOperation(value = "API : To get all the users")
    @GetMapping(value = "/getAllUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return new ResponseEntity<>(
                userServiceInterface.getUsers(),
                HttpStatus.OK
        );
    }

    // API : To switch between owner and borrower *Tested*
    @ApiOperation(value = "API : To switch between owner and borrower")
    @PostMapping(value = "/switch")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    // only owner and borrower can access this
    public ResponseEntity<ApiResponse> switchProfile(
            @RequestParam(value = "role") Role profile,
            HttpServletResponse response,
            HttpServletRequest request
    ) throws UserNotExistException, IOException {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        if (!Helper.notNull(user)) {
            throw new UserNotExistException("User is invalid");
        }

        // Guard statement : To  verify role should be owner or borrower

            user.setRole(profile);
            userServiceInterface.saveProfile(user);
            switchprofile.generateTokenForSwitchProfile(response, profile, request);

            return new ResponseEntity<>(
                    new ApiResponse(
                            true,
                            "Role switch to: " + user.getRole()),
                    HttpStatus.OK
            );
    }

    // API : To update profile of user
    // only owner and borrower can access this

    @ApiOperation(value = "API : To update user profile")
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ApiResponse> updateProfile(
            @RequestBody @Valid ProfileUpdateDto profileUpdateDto,
            HttpServletRequest request
    ) {

        // JWT : Extracting user info from token

        User oldUser = helper.getUser(request);

        if (!Helper.notNull(oldUser)) {
            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            "User not found"
                    ),
                    HttpStatus.NOT_FOUND
            );
        }

        // Calling service layer to update the user

        userServiceInterface.updateProfile(oldUser, profileUpdateDto);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Profile updated successfully"
                ),
                HttpStatus.OK
        );
    }

    // API : To get the current user

    @ApiOperation(value = "API : To get the current user")
    @GetMapping(value = "/getUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<UserDto> getUser(
            HttpServletRequest request
    ) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        // Calling service layer to return current user

        UserDto userDto = userServiceInterface.getUser(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    // API : To update and add user profile picture

    @ApiOperation(value = "API : To update and add user profile picture")
    @PostMapping(value = "/updateProfilePicture", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ApiResponse> profileImage(
            @NonNull @RequestParam(value = "profileImageUrl") String profileImageUrl,
            HttpServletRequest request
    ) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        if (!Helper.notNull(user)) {
            return new ResponseEntity<>(new ApiResponse(false, "User is invalid"), HttpStatus.NOT_FOUND);
        }

        // Calling service layer to save or update profile picture

        userServiceInterface.updateProfileImage(profileImageUrl, user);
        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "Profile picture has been updated."
                ),
                HttpStatus.CREATED);
    }

    // API : To update and add new access token

    @ApiOperation(value = "API : To update and add new access token")
    @PostMapping(value = "/refreshToken")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> updateTokenUsingRefreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);

        // Checking if refresh token is expired or not

        if (securityUtility.isTokenExpired(token)) {
            User user = helper.getUser(request);
            String email = user.getEmail();
            String newAccessToken = securityUtility.updateAccessTokenViaRefreshToken(email, request, token);
            response.setHeader("access_token", newAccessToken);

            return new ResponseEntity<>(new ApiResponse(true, "AccessToken Updated Via RefreshToken"), HttpStatus.CREATED);

            // if refresh token is invalid

        } else {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Refresh Token is expired"
            );

            return new ResponseEntity<>(
                    new ApiResponse(
                            false,
                            " Token is expired"
                    ),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    // API : To log out the current user

    @ApiOperation(value = "API : To log out the current user")
    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> logout(
            HttpServletRequest request
    ) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(request);

        // Extracting email from user

        String email = user.getEmail();

        // Making refresh token invalid

        refreshTokenService.deleteRefreshTokenByEmailAndToken(email, token);

        return new ResponseEntity<>(
                new ApiResponse(
                        true,
                        "User Successfully Logged out "
                ),
                HttpStatus.CREATED);
    }
}


