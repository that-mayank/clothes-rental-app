package com.nineleaps.leaps.dto.user;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@Tag("unit_tests")
@DisplayName("LoginDTO Tests")
class LoginDtoTest {

    @Test
    @DisplayName("Get Email - Valid Email")
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
    @DisplayName("Get Password - Valid Password")
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
    @DisplayName("Set Email - Valid Email")
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
    @DisplayName("Set Password - Valid Password")
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
    @DisplayName("All Args Constructor")
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