package com.nineleaps.leaps.dto.user;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginResponseDtoTest {

    @Test
    void getStatus_validStatus_shouldReturnStatus() {
        // Arrange
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        String status = "SUCCESS";
        loginResponseDto.setStatus(status);

        // Act
        String result = loginResponseDto.getStatus();

        // Assert
        assertEquals(status, result);
    }

    @Test
    void getToken_validToken_shouldReturnToken() {
        // Arrange
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        String token = "abcd1234";
        loginResponseDto.setToken(token);

        // Act
        String result = loginResponseDto.getToken();

        // Assert
        assertEquals(token, result);
    }

    @Test
    void setStatus_validStatus_shouldSetStatus() {
        // Arrange
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        String status = "SUCCESS";

        // Act
        loginResponseDto.setStatus(status);

        // Assert
        assertEquals(status, loginResponseDto.getStatus());
    }

    @Test
    void setToken_validToken_shouldSetToken() {
        // Arrange
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        String token = "abcd1234";

        // Act
        loginResponseDto.setToken(token);

        // Assert
        assertEquals(token, loginResponseDto.getToken());
    }
}
