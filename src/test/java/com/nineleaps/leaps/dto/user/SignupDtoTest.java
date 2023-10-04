package com.nineleaps.leaps.dto.user;

import com.nineleaps.leaps.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignupDtoTest {

    @Test
    void testSetters() {
        // Arrange
        SignupDto signupDto = new SignupDto();
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String phoneNumber = "1234567890";
        String password = "password123";
        Role role = Role.ADMIN;

        // Act
        signupDto.setFirstName(firstName);
        signupDto.setLastName(lastName);
        signupDto.setEmail(email);
        signupDto.setPhoneNumber(phoneNumber);
        signupDto.setPassword(password);
        signupDto.setRole(role);

        // Assert
        assertEquals(firstName, signupDto.getFirstName());
        assertEquals(lastName, signupDto.getLastName());
        assertEquals(email, signupDto.getEmail());
        assertEquals(phoneNumber, signupDto.getPhoneNumber());
        assertEquals(password, signupDto.getPassword());
        assertEquals(role, signupDto.getRole());
    }
}
