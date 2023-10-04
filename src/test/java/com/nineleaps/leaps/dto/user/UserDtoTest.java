package com.nineleaps.leaps.dto.user;


import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import org.junit.jupiter.api.Test;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserDtoTest {

    @Test
    void testConstructor() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("1234567890");
        user.setRole(Role.ADMIN);
        user.setProfileImageUrl("/profile.jpg");

        // Act
        UserDto userDto = new UserDto(user);

        // Assert
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPhoneNumber(), userDto.getPhoneNumber());
        assertEquals(user.getRole(), userDto.getRole());
        assertEquals(NGROK+"/profile.jpg", userDto.getProfileImageUrl());
    }

    @Test
    void testConstructorWithNullImageUrl() {
        // Arrange
        User user = new User();
        user.setProfileImageUrl(null);

        // Act
        UserDto userDto = new UserDto(user);

        // Assert
        assertNull(userDto.getProfileImageUrl());
    }

    @Test
    void testSetters() {
        // Arrange
        UserDto userDto = new UserDto();
        String firstName = "Alice";
        String lastName = "Smith";
        String email = "alice.smith@example.com";
        String phoneNumber = "9876543210";
        Role role = Role.OWNER;
        String profileImageUrl = NGROK+"/profile.jpg";

        // Act
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        userDto.setPhoneNumber(phoneNumber);
        userDto.setRole(role);
        userDto.setProfileImageUrl(profileImageUrl);

        // Assert
        assertEquals(firstName, userDto.getFirstName());
        assertEquals(lastName, userDto.getLastName());
        assertEquals(email, userDto.getEmail());
        assertEquals(phoneNumber, userDto.getPhoneNumber());
        assertEquals(role, userDto.getRole());
        assertEquals(profileImageUrl, userDto.getProfileImageUrl());
    }
}
