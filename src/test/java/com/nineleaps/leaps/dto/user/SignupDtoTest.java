package com.nineleaps.leaps.dto.user;

import com.nineleaps.leaps.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignupDtoTest {

    @Test
    void getFirstName_validFirstName_shouldReturnFirstName() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String firstName = "John";
        signupDto.setFirstName(firstName);

        // Act
        String result = signupDto.getFirstName();

        // Assert
        assertEquals(firstName, result);
    }

    @Test
    void getLastName_validLastName_shouldReturnLastName() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String lastName = "Doe";
        signupDto.setLastName(lastName);

        // Act
        String result = signupDto.getLastName();

        // Assert
        assertEquals(lastName, result);
    }

    @Test
    void getEmail_validEmail_shouldReturnEmail() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String email = "test@example.com";
        signupDto.setEmail(email);

        // Act
        String result = signupDto.getEmail();

        // Assert
        assertEquals(email, result);
    }

    @Test
    void getPhoneNumber_validPhoneNumber_shouldReturnPhoneNumber() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String phoneNumber = "1234567890";
        signupDto.setPhoneNumber(phoneNumber);

        // Act
        String result = signupDto.getPhoneNumber();

        // Assert
        assertEquals(phoneNumber, result);
    }

    @Test
    void getPassword_validPassword_shouldReturnPassword() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String password = "password123";
        signupDto.setPassword(password);

        // Act
        String result = signupDto.getPassword();

        // Assert
        assertEquals(password, result);
    }

    @Test
    void getRole_validRole_shouldReturnRole() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        Role role = Role.ADMIN;
        signupDto.setRole(role);

        // Act
        Role result = signupDto.getRole();

        // Assert
        assertEquals(role, result);
    }

    @Test
    void setFirstName_validFirstName_shouldSetFirstName() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String firstName = "John";

        // Act
        signupDto.setFirstName(firstName);

        // Assert
        assertEquals(firstName, signupDto.getFirstName());
    }

    @Test
    void setLastName_validLastName_shouldSetLastName() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String lastName = "Doe";

        // Act
        signupDto.setLastName(lastName);

        // Assert
        assertEquals(lastName, signupDto.getLastName());
    }

    @Test
    void setEmail_validEmail_shouldSetEmail() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String email = "test@example.com";

        // Act
        signupDto.setEmail(email);

        // Assert
        assertEquals(email, signupDto.getEmail());
    }

    @Test
    void setPhoneNumber_validPhoneNumber_shouldSetPhoneNumber() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String phoneNumber = "1234567890";

        // Act
        signupDto.setPhoneNumber(phoneNumber);

        // Assert
        assertEquals(phoneNumber, signupDto.getPhoneNumber());
    }

    @Test
    void setPassword_validPassword_shouldSetPassword() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String password = "password123";

        // Act
        signupDto.setPassword(password);

        // Assert
        assertEquals(password, signupDto.getPassword());
    }

    @Test
    void setRole_validRole_shouldSetRole() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        Role role = Role.ADMIN;

        // Act
        signupDto.setRole(role);

        // Assert
        assertEquals(role, signupDto.getRole());
    }
}
