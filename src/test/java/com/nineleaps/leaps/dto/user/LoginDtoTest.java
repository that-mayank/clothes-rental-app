package com.nineleaps.leaps.dto.user;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoginDtoTest {

    @Test
    void getEmail_validEmail_shouldReturnEmail() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        String email = "test@example.com";
        loginDto.setEmail(email);

        // Act
        String result = loginDto.getEmail();

        // Assert
        assertEquals(email, result);
    }

    @Test
    void getPassword_validPassword_shouldReturnPassword() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        String password = "password123";
        loginDto.setPassword(password);

        // Act
        String result = loginDto.getPassword();

        // Assert
        assertEquals(password, result);
    }

    @Test
    void setEmail_validEmail_shouldSetEmail() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        String email = "test@example.com";

        // Act
        loginDto.setEmail(email);

        // Assert
        assertEquals(email, loginDto.getEmail());
    }

    @Test
    void setPassword_validPassword_shouldSetPassword() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        String password = "password123";

        // Act
        loginDto.setPassword(password);

        // Assert
        assertEquals(password, loginDto.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String email = "test@example.com";
        String password = "password";

        // Act
        LoginDto loginDto = new LoginDto(email, password);

        // Assert
        assertNotNull(loginDto);
        assertEquals(email, loginDto.getEmail());
        assertEquals(password, loginDto.getPassword());
    }
}