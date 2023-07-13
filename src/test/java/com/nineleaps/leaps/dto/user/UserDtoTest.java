package com.nineleaps.leaps.dto.user;

import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.User;
import org.junit.jupiter.api.Test;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void testConstructorWithNonNullProfileImageUrl() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPhoneNumber("1234567890");
        user.setRole(Role.ADMIN);
        user.setProfileImageUrl("profile-image.jpg");

        // Act
        UserDto userDto = new UserDto(user);

        // Assert
        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPhoneNumber(), userDto.getPhoneNumber());
        assertEquals(user.getRole(), userDto.getRole());
        assertEquals(NGROK+"profile-image.jpg", userDto.getProfileImageUrl());
    }

    @Test
    void testConstructorWithNullProfileImageUrl() {
        // Arrange
        User user = new User();
        user.setId(2L);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("janesmith@example.com");
        user.setPhoneNumber("9876543210");
        user.setRole(Role.ADMIN);
        user.setProfileImageUrl(null);

        // Act
        UserDto userDto = new UserDto(user);

        // Assert
        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPhoneNumber(), userDto.getPhoneNumber());
        assertEquals(user.getRole(), userDto.getRole());
        assertNull(userDto.getProfileImageUrl());
    }

    @Test
    void testToString() {
        // Arrange
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPhoneNumber("1234567890");
        user.setRole(Role.ADMIN);
        user.setProfileImageUrl("profile-image.jpg");
        UserDto userDto = new UserDto(user);

        // Act
        String result = userDto.toString();

        // Assert
        assertEquals("UserDto{firstName='John', lastName='Doe', email='johndoe@example.com', phoneNumber='1234567890', role=ADMIN}", result);
    }

    @Test
    void testEquals_sameObject_shouldReturnTrue() {
        // Arrange
        User user = new User();
        UserDto userDto = new UserDto(user);

        // Act
        boolean result = userDto.equals(userDto);

        // Assert
        assertTrue(result);
    }

    @Test
    void testEquals_differentClass_shouldReturnFalse() {
        // Arrange
        User user = new User();
        UserDto userDto = new UserDto(user);

        // Act
        boolean result = userDto.equals("test");

        // Assert
        assertFalse(result);
    }


    @Test
    void testEquals_differentId_shouldReturnFalse() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);

        // Act
        boolean result = userDto1.equals(userDto2);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEquals_differentFirstName_shouldReturnFalse() {
        // Arrange
        User user1 = new User();
        user1.setFirstName("John");
        User user2 = new User();
        user2.setFirstName("Jane");
        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);

        // Act
        boolean result = userDto1.equals(userDto2);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEquals_differentLastName_shouldReturnFalse() {
        // Arrange
        User user1 = new User();
        user1.setLastName("Doe");
        User user2 = new User();
        user2.setLastName("Smith");
        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);

        // Act
        boolean result = userDto1.equals(userDto2);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEquals_differentEmail_shouldReturnFalse() {
        // Arrange
        User user1 = new User();
        user1.setEmail("johndoe@example.com");
        User user2 = new User();
        user2.setEmail("janesmith@example.com");
        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);

        // Act
        boolean result = userDto1.equals(userDto2);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEquals_differentPhoneNumber_shouldReturnFalse() {
        // Arrange
        User user1 = new User();
        user1.setPhoneNumber("1234567890");
        User user2 = new User();
        user2.setPhoneNumber("9876543210");
        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);

        // Act
        boolean result = userDto1.equals(userDto2);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEquals_differentRole_shouldReturnFalse() {
        // Arrange
        User user1 = new User();
        user1.setRole(Role.ADMIN);
        User user2 = new User();
        user2.setRole(Role.BORROWER);
        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);

        // Act
        boolean result = userDto1.equals(userDto2);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEquals_differentProfileImageUrl_shouldReturnFalse() {
        // Arrange
        User user1 = new User();
        user1.setProfileImageUrl("profile-image1.jpg");
        User user2 = new User();
        user2.setProfileImageUrl("profile-image2.jpg");
        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);

        // Act
        boolean result = userDto1.equals(userDto2);

        // Assert
        assertFalse(result);
    }

    @Test
    void testHashCode_equalObjects_shouldReturnSameHashCode() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(1L);
        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);

        // Act
        int hashCode1 = userDto1.hashCode();
        int hashCode2 = userDto2.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCode_differentObjects_shouldReturnDifferentHashCodes() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);

        // Act
        int hashCode1 = userDto1.hashCode();
        int hashCode2 = userDto2.hashCode();

        // Assert
        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void testSetters() {
        // Arrange
        UserDto userDto = new UserDto(new User());

        // Act
        userDto.setId(1L);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPhoneNumber("1234567890");
        userDto.setRole(Role.BORROWER);
        userDto.setProfileImageUrl("profile.jpg");

        // Assert
        assertEquals(1L, userDto.getId());
        assertEquals("John", userDto.getFirstName());
        assertEquals("Doe", userDto.getLastName());
        assertEquals("john.doe@example.com", userDto.getEmail());
        assertEquals("1234567890", userDto.getPhoneNumber());
        assertEquals(Role.BORROWER, userDto.getRole());
        assertEquals("profile.jpg", userDto.getProfileImageUrl());
    }



    // Other getter/setter tests can be added similarly

}
