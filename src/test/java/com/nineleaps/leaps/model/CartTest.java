package com.nineleaps.leaps.model;

import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.product.ProductUrl;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CartTest {

    @Test
    void testCreateDate() {
        Cart cart = new Cart();
        Date createDate = new Date();
        cart.setCreateDate(createDate);

        assertEquals(createDate, cart.getCreateDate());
    }

    @Test
    void testUserIdNotNull() {
        Cart cart = new Cart();
        User user = new User();
        user.setId(1L);

        cart.setUser(user);

        assertEquals(user, cart.getUser());
    }

    @Test
    void testImageUrlNonNull() {
        Cart cart = new Cart();

        ProductUrl productUrl = new ProductUrl();
        productUrl.setUrl("example-url");

        cart.setImageUrl(productUrl.getUrl());

        assertEquals("example-url", cart.getImageUrl());
    }

    @Test
    void testImageUrlNull() {
        Cart cart = new Cart();

        ProductUrl productUrl = new ProductUrl();


        cart.setImageUrl(productUrl.getUrl());

        assertEquals(null, cart.getImageUrl());
    }

    @Test
    void testImageUrlAssignment() {
        // Create a product
        Product product = new Product();
        // Create a user
        User user = new User();
        // Create a list of ProductUrl
        List<ProductUrl> imageUrlList = new ArrayList<>();
        // Create a ProductUrl
        ProductUrl productUrl = new ProductUrl();
        productUrl.setUrl("https://example.com");
        imageUrlList.add(productUrl);

        // Create a cart with imageUrl not null and not empty
        Cart cartWithImageUrl = new Cart(product, user, 1, LocalDateTime.now(), LocalDateTime.now(), imageUrlList);
        assertEquals("https://example.com", cartWithImageUrl.getImageUrl());

        // Create a cart with imageUrl null or empty
        List<ProductUrl> emptyImageUrlList = new ArrayList<>();
        Cart cartWithEmptyImageUrl = new Cart(product, user, 1, LocalDateTime.now(), LocalDateTime.now(), emptyImageUrlList);
        assertEquals(null, cartWithEmptyImageUrl.getImageUrl());
    }



}
