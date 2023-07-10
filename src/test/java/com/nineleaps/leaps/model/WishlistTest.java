package com.nineleaps.leaps.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class WishlistTest {

    private User user;
    private Product product;
    private Wishlist wishlist;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        product = mock(Product.class);
        wishlist = new Wishlist(product, user);
        wishlist.setId(1L);
        wishlist.setCreateDate(new Date());
    }

    @Test
    void testConstructor() {
        assertNotNull(wishlist.getId());
        assertEquals(user, wishlist.getUser());
        assertEquals(product, wishlist.getProduct());
        assertNotNull(wishlist.getCreateDate());
    }

    @Test
    void testGettersAndSetters() {
        assertNotNull(wishlist.getId());
        assertEquals(user, wishlist.getUser());
        assertEquals(product, wishlist.getProduct());
        assertNotNull(wishlist.getCreateDate());
    }
}
