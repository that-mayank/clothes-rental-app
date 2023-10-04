package com.nineleaps.leaps.model;


import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void testUpdatedAt() {
        LocalDateTime updatedAt = LocalDateTime.now();
        User user = new User();
        user.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void testCreatedBy() {
        Long createdBy = 12345L;
        User user = new User();
        user.setCreatedBy(createdBy);
        assertEquals(createdBy, user.getCreatedBy());
    }

    @Test
    void testUpdatedBy() {
        Long updatedBy = 67890L;
        User user = new User();
        user.setUpdatedBy(updatedBy);
        assertEquals(updatedBy, user.getUpdatedBy());
    }

    @Test
    void testOrders() {
        User user = new User();
        List<Order> orders = new ArrayList<>();
        // Add orders to the list
        user.setOrders(orders);
        assertEquals(orders, user.getOrders());
    }

    @Test
    void testProducts() {
        User user = new User();
        List<Product> products = new ArrayList<>();
        // Add products to the list
        user.setProducts(products);
        assertEquals(products, user.getProducts());
    }

    @Test
    void testUserLoginInfo() {
        User user = new User();
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        // Set userLoginInfo
        user.setUserLoginInfo(userLoginInfo);
        assertEquals(userLoginInfo, user.getUserLoginInfo());
    }

    @Test
    void testProfileUpdateDtoConstructor() {
        // Create a profile update DTO
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        profileUpdateDto.setFirstName("John");
        profileUpdateDto.setLastName("Doe");
        profileUpdateDto.setEmail("john.doe@example.com");
        profileUpdateDto.setPhoneNumber("1234567890");

        // Create an old user
        User oldUser = new User();
        oldUser.setId(1L);  // Assuming ID is set
        oldUser.setFirstName("Old");
        oldUser.setLastName("User");
        oldUser.setEmail("old.user@example.com");
        oldUser.setPhoneNumber("9876543210");

        // Create a new user using the profile update DTO and old user
        User newUser = new User(profileUpdateDto, oldUser);

        // Add assertions to check if properties are set correctly
        assertEquals(oldUser.getId(), newUser.getId());
        assertEquals(profileUpdateDto.getFirstName(), newUser.getFirstName());
        assertEquals(profileUpdateDto.getLastName(), newUser.getLastName());
        assertEquals(profileUpdateDto.getEmail(), newUser.getEmail());
        assertEquals(profileUpdateDto.getPhoneNumber(), newUser.getPhoneNumber());
        // ... add more assertions for other properties if needed
    }
}
