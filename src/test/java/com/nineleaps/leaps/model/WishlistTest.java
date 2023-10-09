package com.nineleaps.leaps.model;

import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@Tag("unit_tests")
@DisplayName("Wishlist Tests")
class WishlistTest {

    @Test
    @DisplayName("Test Wishlist Constructor")
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
