package com.nineleaps.leaps.model;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.enums.Role;

import com.nineleaps.leaps.model.orders.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

 class UserTest {

    @Test
    void testUserGettersAndSetters() {
       // Create a sample User instance
       User user = new User();
       user.setId(1L);
       user.setFirstName("John");
       user.setLastName("Doe");
       user.setEmail("john@example.com");
       user.setPhoneNumber("1234567890");
       user.setPassword("password");
       user.setRole(Role.OWNER);
       user.setProfileImageUrl("https://example.com/profile.jpg");

       // Create a sample list of orders
       List<Order> orders = new ArrayList<>();
       Order order1 = new Order();
       order1.setId(1L);
       orders.add(order1);
       Order order2 = new Order();
       order2.setId(2L);
       orders.add(order2);
       user.setOrders(orders);

       // Create a sample list of addresses
       List<Address> addresses = new ArrayList<>();
       Address address1 = new Address();
       address1.setId(1L);
       addresses.add(address1);
       Address address2 = new Address();
       address2.setId(2L);
       addresses.add(address2);
       user.setAddresses(addresses);

       // Create a sample list of products
       List<Product> products = new ArrayList<>();
       Product product1 = new Product();
       product1.setId(1L);
       products.add(product1);
       Product product2 = new Product();
       product2.setId(2L);
       products.add(product2);
       user.setProducts(products);

       // Verify the getters
       assertEquals(1L, user.getId());
       assertEquals("John", user.getFirstName());
       assertEquals("Doe", user.getLastName());
       assertEquals("john@example.com", user.getEmail());
       assertEquals("1234567890", user.getPhoneNumber());
       assertEquals("password", user.getPassword());
       assertEquals(Role.OWNER, user.getRole());
       assertEquals("https://example.com/profile.jpg", user.getProfileImageUrl());
       assertEquals(orders, user.getOrders());
       assertEquals(addresses, user.getAddresses());
       assertEquals(products, user.getProducts());
    }

    @Test
    void testUserConstructor() {
       // Create a ProfileUpdateDto instance
       ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
       profileUpdateDto.setFirstName("John");
       profileUpdateDto.setLastName("Doe");
       profileUpdateDto.setEmail("john@example.com");
       profileUpdateDto.setPhoneNumber("1234567890");

       // Create an old User instance
       User oldUser = new User();
       oldUser.setId(1L);
       oldUser.setPassword("password");
       oldUser.setRole(Role.OWNER);
       oldUser.setProfileImageUrl("https://example.com/profile.jpg");

       // Create a User instance using the constructor
       User user = new User(profileUpdateDto, oldUser);

       // Verify the values
       assertEquals(oldUser.getId(), user.getId());
       assertEquals(profileUpdateDto.getFirstName(), user.getFirstName());
       assertEquals(profileUpdateDto.getLastName(), user.getLastName());
       assertEquals(profileUpdateDto.getEmail(), user.getEmail());
       assertEquals(profileUpdateDto.getPhoneNumber(), user.getPhoneNumber());
       assertEquals(oldUser.getPassword(), user.getPassword());
       assertEquals(oldUser.getRole(), user.getRole());
       assertEquals(oldUser.getProfileImageUrl(), user.getProfileImageUrl());

    }
}