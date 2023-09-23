package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserLoginInfo;
import com.nineleaps.leaps.repository.UserLoginInfoRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
@Api(tags = "Admin Api", description = "Contains Api for Admin ")
@SuppressWarnings("deprecation")
public class AdminController {

    // Linking layers using constructor injection
    private final UserServiceInterface userService;
    private final UserLoginInfoRepository userLoginInfoRepository;



    // API - Allows the Admin to get all the users
    @ApiOperation(value = "Api to get all the users")
    @GetMapping(value = "/getAllUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')") // Adding Method Level Authorization Via RBAC - Role Based Access Control
    public ResponseEntity<ApiResponse> getAllUsers() {

        // Calling service layer to fetch all users from the database
        userService.getUsers();

        // Status Code : 200 - HttpStatus.OK
        return new ResponseEntity<>(new ApiResponse(true,"Fetched All Users From The Database"),HttpStatus.OK);
    }


    // Re-Activate Locked-Account
    @PreAuthorize("hasAuthority('ADMIN')") // Adding Method Level Authorization Via RBAC - Role Based Access Control
    @PostMapping(value = "/activate-account",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> activateAccount(@RequestParam(name = "user-email") @Valid String email) {
        try {
            // Calling service layer to fetch user by email
            User user = userService.getUser(email);

            // check if the user is null
            if (user == null) {

                // Status Code : 404 - HttpStatus.NOT_FOUND
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            // Calling service layer to fetch user-login-info details
            UserLoginInfo userLoginInfo = userLoginInfoRepository.findByUserId(user.getId());



                // Reset the user-login-info details for the user
                userLoginInfo.resetLoginAttempts();

                // Save the user-login-info details
                userLoginInfoRepository.save(userLoginInfo);


            // Status Code : 202 - HttpStatus.ACCEPTED
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("User account Re-Activated successfully.");
        } catch (Exception e) {

            //Status Code : 500 = HttpStatus.INTERNAL_SERVER_ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
}
