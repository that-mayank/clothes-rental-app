package com.nineleaps.leaps.model;

import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WishlistTest {

    @Test
    void testWishlistConstructor() {
        // Create a user
        User user = new User();
        user.setId(1L);  // Assuming ID is set

        // Create a product
        Product product = new Product();
        product.setId(100L);  // Assuming ID is set

        // Create a wishlist using the product and user
        Wishlist wishlist = new Wishlist(product, user);

        // Add assertions to check if properties are set correctly
        assertEquals(user, wishlist.getUser());
        assertEquals(product, wishlist.getProduct());
        assertNotNull(wishlist.getCreateDate());

    }
}
