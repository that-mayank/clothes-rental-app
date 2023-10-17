package com.nineleaps.leaps.dto.user;

import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest {

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(new User());
    }

    @Test
    void getId() {
        userDto.setId(1L);
        assertEquals(1L, userDto.getId());
    }

    @Test
    void getFirstName() {
        userDto.setFirstName("John");
        assertEquals("John", userDto.getFirstName());
    }

    @Test
    void getLastName() {
        userDto.setLastName("Doe");
        assertEquals("Doe", userDto.getLastName());
    }

    @Test
    void getEmail() {
        userDto.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", userDto.getEmail());
    }

    @Test
    void getPhoneNumber() {
        userDto.setPhoneNumber("1234567890");
        assertEquals("1234567890", userDto.getPhoneNumber());
    }

    @Test
    void getRole() {
        userDto.setRole(Role.BORROWER);
        assertEquals(Role.BORROWER, userDto.getRole());
    }

    @Test
    void getProfileImageUrl() {
        userDto.setProfileImageUrl("profile.jpg");
        assertEquals("profile.jpg", userDto.getProfileImageUrl());
    }

    @Test
    void setId() {
        userDto.setId(2L);
        assertEquals(2L, userDto.getId());
    }

    @Test
    void setFirstName() {
        userDto.setFirstName("Alice");
        assertEquals("Alice", userDto.getFirstName());
    }

    @Test
    void setLastName() {
        userDto.setLastName("Smith");
        assertEquals("Smith", userDto.getLastName());
    }

    @Test
    void setEmail() {
        userDto.setEmail("alice.smith@example.com");
        assertEquals("alice.smith@example.com", userDto.getEmail());
    }

    @Test
    void setPhoneNumber() {
        userDto.setPhoneNumber("9876543210");
        assertEquals("9876543210", userDto.getPhoneNumber());
    }

    @Test
    void setRole() {
        userDto.setRole(Role.ADMIN);
        assertEquals(Role.ADMIN, userDto.getRole());
    }

    @Test
    void setProfileImageUrl() {
        userDto.setProfileImageUrl("avatar.jpg");
        assertEquals("avatar.jpg", userDto.getProfileImageUrl());
    }
}
