package com.nineleaps.leaps.dto.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileUpdateDtoTest {

    private ProfileUpdateDto profileUpdateDto;

    @BeforeEach
    void setUp() {
        profileUpdateDto = new ProfileUpdateDto();
    }

    @Test
    void getFirstName() {
        profileUpdateDto.setFirstName("John");
        assertEquals("John", profileUpdateDto.getFirstName());
    }

    @Test
    void getLastName() {
        profileUpdateDto.setLastName("Doe");
        assertEquals("Doe", profileUpdateDto.getLastName());
    }

    @Test
    void getEmail() {
        profileUpdateDto.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", profileUpdateDto.getEmail());
    }

    @Test
    void getPhoneNumber() {
        profileUpdateDto.setPhoneNumber("1234567890");
        assertEquals("1234567890", profileUpdateDto.getPhoneNumber());
    }

    @Test
    void setFirstName() {
        profileUpdateDto.setFirstName("Alice");
        assertEquals("Alice", profileUpdateDto.getFirstName());
    }

    @Test
    void setLastName() {
        profileUpdateDto.setLastName("Smith");
        assertEquals("Smith", profileUpdateDto.getLastName());
    }

    @Test
    void setEmail() {
        profileUpdateDto.setEmail("alice.smith@example.com");
        assertEquals("alice.smith@example.com", profileUpdateDto.getEmail());
    }

    @Test
    void setPhoneNumber() {
        profileUpdateDto.setPhoneNumber("9876543210");
        assertEquals("9876543210", profileUpdateDto.getPhoneNumber());
    }
}
