package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.UserLoginInfo;
import com.nineleaps.leaps.repository.UserLoginInfoRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Tag("unit_tests")
@DisplayName("Test case file for admin controller")
class AdminControllerTest {

    @Mock
    private UserServiceInterface userService;

    @Mock
    private UserLoginInfoRepository userLoginInfoRepository;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("get all users")
    void getAllUsers() {
        // Call the method
        ResponseEntity<ApiResponse> response = adminController.getAllUsers();

        // Verify the userService.getUsers() method is called
        verify(userService).getUsers();

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Fetched All Users From The Database", response.getBody().getMessage());
    }


    @Test
    @DisplayName("Activate account")
    void activateAccount_UserExists_AccountActivated() {
        // Mock data
        String userEmail = "test@example.com";
        User user = new User();
        user.setId(1L);  // Set a user ID for testing purposes
        UserLoginInfo userLoginInfo = new UserLoginInfo();

        // Mock behavior
        when(userService.getUser(anyString())).thenReturn(user);
        when(userLoginInfoRepository.findByUserId(anyLong())).thenReturn(userLoginInfo);

        // Capture the argument passed to save method
        ArgumentCaptor<UserLoginInfo> userLoginInfoCaptor = ArgumentCaptor.forClass(UserLoginInfo.class);

        // Call the method
        ResponseEntity<String> response = adminController.activateAccount(userEmail);

        // Assert the response
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("User account Re-Activated successfully.", response.getBody());

        // Verify that the save method is invoked with the correct argument
        verify(userLoginInfoRepository).save(userLoginInfoCaptor.capture());

        // Assert the captured argument matches the expected userLoginInfo
        assertEquals(userLoginInfo, userLoginInfoCaptor.getValue());
    }



    @Test
    @DisplayName("Activate account - user not found ")
    void activateAccount_UserNotFound_ReturnsNotFound() {
        // Mock data
        String userEmail = "notfound@example.com";

        // Mock behavior
        when(userService.getUser(anyString())).thenReturn(null);

        // Call the method
        ResponseEntity<String> response = adminController.activateAccount(userEmail);

        // Assert the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    @DisplayName("Activate account - failed Internal server error")
    void activateAccount_InternalError_ReturnsInternalServerError() {
        // Mock data
        String userEmail = "test@example.com";

        // Mock behavior
        when(userService.getUser(anyString())).thenThrow(new RuntimeException("Internal error"));

        // Call the method
        ResponseEntity<String> response = adminController.activateAccount(userEmail);

        // Assert the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred.", response.getBody());
    }
}
