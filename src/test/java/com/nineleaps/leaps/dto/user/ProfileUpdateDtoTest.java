package com.nineleaps.leaps.dto.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileUpdateDtoTest {

    @Test
    void getFirstName_validFirstName_shouldReturnFirstName() {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        String firstName = "John";
        profileUpdateDto.setFirstName(firstName);

        // Act
        String result = profileUpdateDto.getFirstName();

        // Assert
        assertEquals(firstName, result);
    }

    @Test
    void getLastName_validLastName_shouldReturnLastName() {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        String lastName = "Doe";
        profileUpdateDto.setLastName(lastName);

        // Act
        String result = profileUpdateDto.getLastName();

        // Assert
        assertEquals(lastName, result);
    }

    @Test
    void getEmail_validEmail_shouldReturnEmail() {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        String email = "johndoe@example.com";
        profileUpdateDto.setEmail(email);

        // Act
        String result = profileUpdateDto.getEmail();

        // Assert
        assertEquals(email, result);
    }

    @Test
    void getPhoneNumber_validPhoneNumber_shouldReturnPhoneNumber() {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        String phoneNumber = "1234567890";
        profileUpdateDto.setPhoneNumber(phoneNumber);

        // Act
        String result = profileUpdateDto.getPhoneNumber();

        // Assert
        assertEquals(phoneNumber, result);
    }

    @Test
    void setFirstName_validFirstName_shouldSetFirstName() {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        String firstName = "John";

        // Act
        profileUpdateDto.setFirstName(firstName);

        // Assert
        assertEquals(firstName, profileUpdateDto.getFirstName());
    }

    @Test
    void setLastName_validLastName_shouldSetLastName() {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        String lastName = "Doe";

        // Act
        profileUpdateDto.setLastName(lastName);

        // Assert
        assertEquals(lastName, profileUpdateDto.getLastName());
    }

    @Test
    void setEmail_validEmail_shouldSetEmail() {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        String email = "johndoe@example.com";

        // Act
        profileUpdateDto.setEmail(email);

        // Assert
        assertEquals(email, profileUpdateDto.getEmail());
    }

    @Test
    void setPhoneNumber_validPhoneNumber_shouldSetPhoneNumber() {
        // Arrange
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        String phoneNumber = "1234567890";

        // Act
        profileUpdateDto.setPhoneNumber(phoneNumber);

        // Assert
        assertEquals(phoneNumber, profileUpdateDto.getPhoneNumber());
    }
}
