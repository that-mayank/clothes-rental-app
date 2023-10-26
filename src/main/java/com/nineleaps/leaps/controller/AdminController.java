package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserLoginInfo;
import com.nineleaps.leaps.repository.UserLoginInfoRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

        try {
            // Calling service layer to fetch all users from the database
            userService.getUsers();
            log.info("Fetched All Users From The Database");
            return new ResponseEntity<>(new ApiResponse(true, "Fetched All Users From The Database"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while fetching all users", e);
            return new ResponseEntity<>(new ApiResponse(false, "Failed to fetch users"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Re-Activate Locked-Account
    @PreAuthorize("hasAuthority('ADMIN')") // Adding Method Level Authorization Via RBAC - Role Based Access Control
    @PostMapping(value = "/activate-account",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> activateAccount(@RequestParam(name = "user-email") @Valid String email) {
        try {
            // Calling service layer to fetch user by email
            User user = userService.getUser(email);

            if (user == null) {
                log.warn("User not found for email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            // Calling service layer to fetch user-login-info details
            UserLoginInfo userLoginInfo = userLoginInfoRepository.findByUserId(user.getId());

            // Reset the user-login-info details for the user
            userLoginInfo.resetLoginAttempts();

            // Save the user-login-info details
            userLoginInfoRepository.save(userLoginInfo);

            log.info("User account Re-Activated successfully for email: {}", email);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("User account Re-Activated successfully.");
        } catch (Exception e) {
            log.error("Error while activating user account for email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
}
