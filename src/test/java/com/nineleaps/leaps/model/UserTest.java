package com.nineleaps.leaps.model;

import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void getId() {
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    void getFirstName() {
        user.setFirstName("John");
        assertEquals("John", user.getFirstName());
    }

    @Test
    void getLastName() {
        user.setLastName("Doe");
        assertEquals("Doe", user.getLastName());
    }

    @Test
    void getEmail() {
        user.setEmail("john@example.com");
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void getPhoneNumber() {
        user.setPhoneNumber("123-456-7890");
        assertEquals("123-456-7890", user.getPhoneNumber());
    }

    @Test
    void getPassword() {
        user.setPassword("password123");
        assertEquals("password123", user.getPassword());
    }

    @Test
    void getRole() {
        user.setRole(Role.BORROWER);
        assertEquals(Role.BORROWER, user.getRole());
    }

    @Test
    void getProfileImageUrl() {
        user.setProfileImageUrl("profile.jpg");
        assertEquals("profile.jpg", user.getProfileImageUrl());
    }

    @Test
    void getDeviceToken() {
        user.setDeviceToken("device-token-123");
        assertEquals("device-token-123", user.getDeviceToken());
    }

    @Test
    void getOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        user.setOrders(orders);
        assertEquals(orders, user.getOrders());
    }

    @Test
    void getAddresses() {
        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address());
        user.setAddresses(addresses);
        assertEquals(addresses, user.getAddresses());
    }

    @Test
    void getProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product());
        user.setProducts(products);
        assertEquals(products, user.getProducts());
    }

    @Test
    void setId() {
        user.setId(2L);
        assertEquals(2L, user.getId());
    }

    @Test
    void setFirstName() {
        user.setFirstName("Jane");
        assertEquals("Jane", user.getFirstName());
    }

    @Test
    void setLastName() {
        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());
    }

    @Test
    void setEmail() {
        user.setEmail("jane@example.com");
        assertEquals("jane@example.com", user.getEmail());
    }

    @Test
    void setPhoneNumber() {
        user.setPhoneNumber("987-654-3210");
        assertEquals("987-654-3210", user.getPhoneNumber());
    }

    @Test
    void setPassword() {
        user.setPassword("newPassword");
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void setRole() {
        user.setRole(Role.ADMIN);
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void setProfileImageUrl() {
        user.setProfileImageUrl("new_profile.jpg");
        assertEquals("new_profile.jpg", user.getProfileImageUrl());
    }

    @Test
    void setDeviceToken() {
        user.setDeviceToken("new-device-token-456");
        assertEquals("new-device-token-456", user.getDeviceToken());
    }

    @Test
    void setOrders() {
        List<Order> newOrders = new ArrayList<>();
        newOrders.add(new Order());
        user.setOrders(newOrders);
        assertEquals(newOrders, user.getOrders());
    }

    @Test
    void setAddresses() {
        List<Address> newAddresses = new ArrayList<>();
        newAddresses.add(new Address());
        user.setAddresses(newAddresses);
        assertEquals(newAddresses, user.getAddresses());
    }

    @Test
    void setProducts() {
        List<Product> newProducts = new ArrayList<>();
        newProducts.add(new Product());
        user.setProducts(newProducts);
        assertEquals(newProducts, user.getProducts());
    }
}
