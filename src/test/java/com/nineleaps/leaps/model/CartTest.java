package com.nineleaps.leaps.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

 class CartTest {

    @Test
     void testCartGettersAndSetters() {
        // Create a sample Cart instance
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCreateDate(new Date());
        Product product = new Product();
        product.setId(1L);
        cart.setProduct(product);
        User user = new User();
        user.setId(1L);
        cart.setUser(user);
        cart.setQuantity(2);
        LocalDateTime rentalStartDate = LocalDateTime.now();
        cart.setRentalStartDate(rentalStartDate);
        LocalDateTime rentalEndDate = rentalStartDate.plusDays(7);
        cart.setRentalEndDate(rentalEndDate);
        List<ProductUrl> imageUrlList = new ArrayList<>();
        ProductUrl productUrl = new ProductUrl();
        productUrl.setUrl("https://example.com/image.jpg");
        imageUrlList.add(productUrl);
        cart.setImageUrl(productUrl.getUrl());

        // Verify the getters
        assertEquals(1L, cart.getId());
        assertNotNull(cart.getCreateDate());
        assertEquals(product, cart.getProduct());
        assertEquals(user, cart.getUser());
        assertEquals(2, cart.getQuantity());
        assertEquals(rentalStartDate, cart.getRentalStartDate());
        assertEquals(rentalEndDate, cart.getRentalEndDate());
        assertEquals("https://example.com/image.jpg", cart.getImageUrl());
    }

    @Test
     void testCartConstructor() {
        // Create a sample Product instance
        Product product = new Product();
        product.setId(1L);

        // Create a sample User instance
        User user = new User();
        user.setId(1L);

        // Create a sample LocalDateTime instance
        LocalDateTime rentalStartDate = LocalDateTime.now();
        LocalDateTime rentalEndDate = rentalStartDate.plusDays(7);

        // Create a sample ProductUrl instance
        ProductUrl productUrl = new ProductUrl();
        productUrl.setUrl("https://example.com/image.jpg");

        // Create a list of ProductUrl instances
        List<ProductUrl> imageUrlList = new ArrayList<>();
        imageUrlList.add(productUrl);

        // Create a Cart instance using the constructor
        Cart cart = new Cart(product, user, 2, rentalStartDate, rentalEndDate, imageUrlList);

        // Verify the values
        assertNotNull(cart.getCreateDate());
        assertEquals(product, cart.getProduct());
        assertEquals(user, cart.getUser());
        assertEquals(2, cart.getQuantity());
        assertEquals(rentalStartDate, cart.getRentalStartDate());
        assertEquals(rentalEndDate, cart.getRentalEndDate());
        assertEquals("https://example.com/image.jpg", cart.getImageUrl());
    }
}