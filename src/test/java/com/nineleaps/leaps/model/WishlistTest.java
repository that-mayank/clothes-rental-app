package com.nineleaps.leaps.model;

import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WishlistTest {

    private Wishlist wishlist;

    @BeforeEach
    void setUp() {
        wishlist = new Wishlist();
    }

    @Test
    void getId() {
        wishlist.setId(1L);
        assertEquals(1L, wishlist.getId());
    }

    @Test
    void getUser() {
        User user = new User();
        wishlist.setUser(user);
        assertEquals(user, wishlist.getUser());
    }

    @Test
    void getProduct() {
        Product product = new Product();
        wishlist.setProduct(product);
        assertEquals(product, wishlist.getProduct());
    }

    @Test
    void getCreateDate() {
        Date createDate = new Date();
        wishlist.setCreateDate(createDate);
        assertEquals(createDate, wishlist.getCreateDate());
    }

    @Test
    void setId() {
        wishlist.setId(2L);
        assertEquals(2L, wishlist.getId());
    }

    @Test
    void setUser() {
        User user = new User();
        wishlist.setUser(user);
        assertEquals(user, wishlist.getUser());
    }

    @Test
    void setProduct() {
        Product product = new Product();
        wishlist.setProduct(product);
        assertEquals(product, wishlist.getProduct());
    }

    @Test
    void setCreateDate() {
        Date createDate = new Date();
        wishlist.setCreateDate(createDate);
        assertEquals(createDate, wishlist.getCreateDate());
    }

    @Test
    void constructorWithParameters() {
        Product product = new Product();
        User user = new User();
        wishlist = new Wishlist(product, user);
        wishlist.setId(1L);

        assertNotNull(wishlist.getId());
        assertEquals(product, wishlist.getProduct());
        assertEquals(user, wishlist.getUser());
        assertNotNull(wishlist.getCreateDate());
    }
}
